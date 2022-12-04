package site.iswho.wh.argumentResolver;

import java.lang.reflect.Method;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import site.iswho.wh.annotation.EnjoyService;

@EnjoyService("httpServletResponseArgResolver")
public class HttpServletResponserArgResolver implements ArgumentResolver{

  @Override
  public boolean support(Class<?> type, int index, Method method) {

    return ServletRequest.class.isAssignableFrom(type);
  }

  @Override
  public Object argumentResolver(HttpServletRequest request, HttpServletResponse response,
      Class<?> type, int index, Method method) {
    return response;
  }
}
