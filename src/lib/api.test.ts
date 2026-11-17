import { afterEach, describe, expect, it, vi } from "vitest";
import { api, apiFile, checkApiConnection, token } from "@/lib/api";

afterEach(() => {
  token.clear();
  localStorage.removeItem("imageseal-user");
  vi.unstubAllGlobals();
});

describe("ImageSeal API requests", () => {
  it("sends authenticated verification requests to the Spring API on port 8081", async () => {
    token.set("test-jwt");
    const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify({ matched: true }), { status: 200 }));
    vi.stubGlobal("fetch", fetchMock);
    const form = new FormData();
    form.append("file", new Blob(["image bytes"], { type: "image/png" }), "image.png");

    await expect(api<{ matched: boolean }>("/images/image-id/verify", { method: "POST", body: form })).resolves.toEqual({ matched: true });

    expect(fetchMock).toHaveBeenCalledOnce();
    const [url, init] = fetchMock.mock.calls[0] as [string, RequestInit];
    expect(url).toBe("http://localhost:8081/api/images/image-id/verify");
    expect(new Headers(init.headers).get("Authorization")).toBe("Bearer test-jwt");
    expect(new Headers(init.headers).has("Content-Type")).toBe(false);
  });

  it("preserves backend image errors instead of hiding them", async () => {
    vi.stubGlobal("fetch", vi.fn().mockResolvedValue(new Response(JSON.stringify({ message: "The image file is missing from server storage." }), { status: 404 })));

    await expect(apiFile("/files/image-id")).rejects.toThrow("The image file is missing from server storage.");
  });

  it("checks the public health endpoint without sending a JWT", async () => {
    token.set("test-jwt");
    const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify({ status: "UP" }), { status: 200 }));
    vi.stubGlobal("fetch", fetchMock);

    await expect(checkApiConnection()).resolves.toBe(true);

    const [url, init] = fetchMock.mock.calls[0] as [string, RequestInit];
    expect(url).toBe("http://localhost:8081/api/health");
    expect(new Headers(init.headers).has("Authorization")).toBe(false);
  });
});
