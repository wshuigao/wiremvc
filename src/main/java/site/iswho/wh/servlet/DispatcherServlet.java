package site.iswho.wh.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import site.iswho.wh.annotation.EnjoyController;
import site.iswho.wh.annotation.EnjoyQualifier;
import site.iswho.wh.annotation.EnjoyRequestMapping;
import site.iswho.wh.annotation.EnjoyService;
import site.iswho.wh.controller.WhController;
import site.iswho.wh.hand.HandToolsService;

public class DispatcherServlet extends HttpServlet {
  List<String> classNames = new ArrayList<>();
  Map<String,Object> beans = new HashMap<>();
  Map<String,Object> handMap = new HashMap<>();

  public void init(ServletConfig config){
    // 1.扫描哪些类需要被实例化
      doScanPackage("site.iswho");
      for (String cname:classNames) {
        System.out.println(cname);
      }
    // 2.classNames所有bean全类名路径
      doInstance();
    // 3.依赖注入
    iocDi();
    // 4.建立一个URL 与 method的映射关系
    whHandMapper();
    for(Map.Entry< String,Object> entry:handMap.entrySet()){
      System.out.println(entry.getKey()+":"+entry.getValue());
    }

  }




  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    this.doPost(req, resp);
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
      String uri = req.getRequestURI();
      String context = req.getContextPath();
      String path = uri.replaceAll(context, "");
      Method method = (Method) handMap.get(path);
      WhController instance = (WhController) beans.get("/"+path.split("/")[1]);

      // 策略处理器
    HandToolsService hand = (HandToolsService) beans.get("whHandTool");

      Object[] args = hand.hand(req,resp,method,beans);
    try {
      method.invoke(instance,args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }

  }

  private  void doScanPackage(String basePackage){
    URL url = this.getClass().getClassLoader()
          .getResource("/"+basePackage.replaceAll("\\.","/"));
    assert url != null;
    String fileStr = url.getFile();
    File f = new File(fileStr);
    String[] files = f.list();
    assert files != null;
    for (String str:files) {
      File filePath = new File(fileStr + str);
      if(f.isDirectory()){
        doScanPackage(basePackage+"."+filePath);
      }else {
        classNames.add(basePackage+"."+filePath.getName());
      }
    }
  }

  public void doInstance() {
    if(classNames.size() <= 0){
      System.out.println("doScanFailed。。。。。");
      return;
    }

    for(String className : classNames){
      String cn = className.replaceAll(".class", "");

      try {
        Class<?> clazz = Class.forName(cn);

        if(clazz.isAnnotationPresent(EnjoyController.class)){
          EnjoyController enjoyController = clazz.getAnnotation(EnjoyController.class);
          // 拿到实例化的bean
          Object instance = clazz.newInstance();
          EnjoyRequestMapping requestMapping = clazz.getAnnotation(EnjoyRequestMapping.class);
          String key = requestMapping.value();
          beans.put(key,instance);
        }else if (clazz.isAnnotationPresent(EnjoyService.class)){
          Object instance = clazz.newInstance();
          EnjoyService service = clazz.getAnnotation(EnjoyService.class);
          String key = service.value();
          beans.put(key,instance);
        }else {
          continue;
        }
      } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void iocDi() {
      if(beans.entrySet().size() <= 0){
        System.out.println("类没有被实例化");
        return;
      }
      // 把service注入到controller
    for(Map.Entry< String,Object> entry:beans.entrySet()){
      Object instance = entry.getValue();
      // 获取类，获取类声明了哪些注解
      Class< ? > clazz = instance.getClass();
      if(clazz.isAnnotationPresent(EnjoyController.class)){
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
          if(field.isAnnotationPresent(EnjoyQualifier.class)){
            EnjoyQualifier qualifier = field.getAnnotation(EnjoyQualifier.class);
            String value = qualifier.value();
            // 放开权限
            field.setAccessible(true);
            try {
              field.set(instance,beans.get(getServletInfo()));
            } catch (IllegalAccessException e) {
              throw new RuntimeException(e);
            }
          }else {
            continue;
          }
        }
      }else {
        continue;
      }
    }
  }

  // 建立映射关系
  private void whHandMapper() {
    if(beans.entrySet().size() <= 0){
      System.out.println("类没有被实例化");
      return;
    }
    for(Map.Entry< String,Object> entry:beans.entrySet()){
      Object instance = entry.getValue();
      Class< ? > clazz = instance.getClass();
      if(clazz.isAnnotationPresent(EnjoyController.class)){
        EnjoyRequestMapping requestMapping = clazz.getAnnotation(EnjoyRequestMapping.class);
        String classPath = requestMapping.value();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
          if (method.isAnnotationPresent(EnjoyRequestMapping.class)) {
            EnjoyRequestMapping mapping = method.getAnnotation(EnjoyRequestMapping.class);
            String methodUrl = mapping.value();
            handMap.put(classPath+methodUrl,method);
          }else {
            continue;
          }
        }
      }
    }



  }


}
