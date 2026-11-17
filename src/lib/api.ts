const API_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8081/api";
const TOKEN_KEY = "imageseal-token";
export const token = { get: () => localStorage.getItem(TOKEN_KEY), set: (value: string) => localStorage.setItem(TOKEN_KEY, value), clear: () => localStorage.removeItem(TOKEN_KEY) };
export function currentUserEmail(): string | null { try { const raw=localStorage.getItem("imageseal-user"); return raw ? (JSON.parse(raw) as {email:string}).email : null; } catch { return null; } }
export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers); const auth = token.get(); if (auth) headers.set("Authorization", `Bearer ${auth}`);
  if (init.body && !(init.body instanceof FormData)) headers.set("Content-Type", "application/json");
  let response: Response;
  try {
    response = await fetch(`${API_URL}${path}`, { ...init, headers });
  } catch (error) {
    if (error instanceof TypeError) throw new Error(`Cannot reach the ImageSeal API at ${API_URL}. Check that Spring Boot is running on port 8081 and that CORS allows this frontend.`);
    throw error;
  }
  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body: unknown = await response.json();
      if (body && typeof body === "object" && "message" in body && typeof body.message === "string") message = body.message;
    } catch {}
    if (response.status === 401) { token.clear(); localStorage.removeItem("imageseal-user"); }
    throw new Error(message);
  }
  if (response.status === 204) return undefined as T; return response.json() as Promise<T>;
}
export async function authenticate(mode: "login" | "register", email: string, password: string) {
  await checkApiConnection();
  const result = await api<{token:string;user:{id:string;email:string}}>(`/auth/${mode}`, {method:"POST", body:JSON.stringify({email,password})});
  token.set(result.token); localStorage.setItem("imageseal-user", JSON.stringify(result.user)); return result;
}
export const apiUrl = API_URL;
/** Public connectivity probe; this deliberately does not send a JWT. */
export async function checkApiConnection(): Promise<boolean> {
  let response: Response;
  try {
    response = await fetch(`${API_URL}/health`, { method: "GET", headers: { Accept: "application/json" }, cache: "no-store" });
  } catch (error) {
    if (error instanceof TypeError) throw new Error(`Cannot reach the ImageSeal API at ${API_URL}. Check that Spring Boot is running on port 8081 and that CORS allows this frontend.`);
    throw error;
  }
  if (!response.ok) throw new Error(`ImageSeal API health check failed (HTTP ${response.status}).`);
  const body: unknown = await response.json();
  if (!body || typeof body !== "object" || !("status" in body) || body.status !== "UP") {
    throw new Error("ImageSeal API responded, but its health status was not UP.");
  }
  return true;
}
export async function apiFile(path: string): Promise<Blob> {
  const headers = new Headers(); const auth=token.get(); if(auth) headers.set("Authorization",`Bearer ${auth}`);
  let response: Response;
  try { response=await fetch(`${API_URL}${path}`,{headers}); }
  catch (error) {
    if (error instanceof TypeError) throw new Error(`Cannot reach the ImageSeal API at ${API_URL}. Check that Spring Boot is running on port 8081 and that CORS allows this frontend.`);
    throw error;
  }
  if(!response.ok) {
    let message = `Could not load the stored image (HTTP ${response.status}).`;
    try {
      const body: unknown = await response.json();
      if (body && typeof body === "object" && "message" in body && typeof body.message === "string") message = body.message;
    } catch {}
    if (response.status === 401) { token.clear(); localStorage.removeItem("imageseal-user"); }
    throw new Error(message);
  }
  return response.blob();
}
