package site.iswho.wh.service.impl;

import site.iswho.wh.annotation.EnjoyService;
import site.iswho.wh.service.WhService;

@EnjoyService("WhServiceImpl")
public class WhServiceImpl implements WhService {

  @Override
  public String query(String name, String age) {
    return "name=="+name+";age=="+age;
  }

  @Override
  public String insert(String params) {
    return "insert success";
  }

  @Override
  public String update(String params) {
    return "update success";
  }
}
