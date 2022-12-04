package site.iswho.wh.argumentResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import site.iswho.wh.annotation.EnjoyRequestParam;
import site.iswho.wh.annotation.EnjoyService;

@EnjoyService("requestParamArgResolver")
public class RequestParamArgResolver implements ArgumentResolver{

  @Override
  public boolean support(Class<?> type, int index, Method method) {

    Annotation[][] anno = method.getParameterAnnotations();
    Annotation[] paramsAnnos = anno[index];
    for (Annotation an: paramsAnnos) {
        if(EnjoyRequestParam.class.isAssignableFrom(an.getClass())){
          return true;
        }
    }

    return false;
  }

  @Override
  public Object argumentResolver(HttpServletRequest request, HttpServletResponse response,
      Class<?> type, int index, Method method) {
    Annotation[][] anno = method.getParameterAnnotations();
    Annotation[] paramsAnnos = anno[index];
    for (Annotation an: paramsAnnos) {
      if(EnjoyRequestParam.class.isAssignableFrom(an.getClass())){
        EnjoyRequestParam er = (EnjoyRequestParam) an;
        String value = er.value();
        return request.getParameter(value);
      }
    }

    return null;
  }
}
