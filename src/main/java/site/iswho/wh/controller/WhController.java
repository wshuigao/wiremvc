package site.iswho.wh.controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import site.iswho.wh.annotation.EnjoyController;
import site.iswho.wh.annotation.EnjoyQualifier;
import site.iswho.wh.annotation.EnjoyRequestMapping;
import site.iswho.wh.annotation.EnjoyRequestParam;
import site.iswho.wh.service.WhService;

@EnjoyController
@EnjoyRequestMapping("/wh")
public class WhController {

  @EnjoyQualifier("WhServiceImpl")
  private WhService whService;

  @EnjoyRequestMapping("/query")
  public void query(HttpServletRequest request, HttpServletResponse response,
      @EnjoyRequestParam("name") String name,@EnjoyRequestParam("age") String age) throws IOException {
      PrintWriter writer = response.getWriter();
      String result = whService.query(name,age);
      writer.write(result);
  }
  

  public void test() {
    System.out.println("test");

  }

}
