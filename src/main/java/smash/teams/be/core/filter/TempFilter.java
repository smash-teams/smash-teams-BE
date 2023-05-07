package smash.teams.be.core.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

@Slf4j
public class TempFilter implements Filter { // 예시 필터, 아무런 기능 없음.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.debug("디버그 : TempFilter 동작");
        chain.doFilter(request, response);
    }
}
