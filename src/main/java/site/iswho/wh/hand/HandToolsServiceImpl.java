package site.iswho.wh.hand;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import site.iswho.wh.annotation.EnjoyService;
import site.iswho.wh.argumentResolver.ArgumentResolver;

@EnjoyService("whHandTool")
public class HandToolsServiceImpl implements HandToolsService {

  @Override
  public Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method,
      Map<String, Object> beans) {
    Class< ? >[] paramsClazzs = method.getParameterTypes();
    Object[] args = new Object[paramsClazzs.length];

    Map<String,Object> argReslovers = getInstanceType(beans, ArgumentResolver.class);

    int index = 0 , i = 0;
    for (Class<?> paramClazz:paramsClazzs){
      for(Map.Entry< String,Object> entry:argReslovers.entrySet()){
        ArgumentResolver ar = (ArgumentResolver) entry.getValue();
        if(ar.support(paramClazz,index,method)){
            args[i++] = ar.argumentResolver(request,response,paramClazz,index,method);
        }
      }
      index++;
    }

    return new Object[0];
  }

  private Map<String, Object> getInstanceType(Map<String, Object> beans, Class<?> type) {
    Map<String,Object> resultBeans = new HashMap<>();
    for (Map.Entry<String, Object> entry : beans.entrySet()) {
      Class<?>[] infs = entry.getValue().getClass().getInterfaces();
      if(infs != null && infs.length > 0){
        for (Class<?> inf:infs) {
          if(inf.isAssignableFrom(type)){
            resultBeans.put(entry.getKey(),entry.getValue());
          }
        }
      }
    }
    return resultBeans;
  }
}
