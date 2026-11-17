package com.imageseal.security;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt; public JwtAuthFilter(JwtService jwt){this.jwt=jwt;}
    @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain)throws ServletException,IOException{
        String auth=req.getHeader("Authorization"); if(auth!=null&&auth.startsWith("Bearer ")) try { Claims claims=jwt.parse(auth.substring(7)); var principal=new UserPrincipal(claims.getSubject(),claims.get("email",String.class)); SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,null,AuthorityUtils.NO_AUTHORITIES)); } catch(Exception ignored) { SecurityContextHolder.clearContext(); }
        chain.doFilter(req,res);
    }
}
