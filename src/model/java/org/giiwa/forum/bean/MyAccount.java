package org.giiwa.forum.bean;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Column;
import org.giiwa.core.bean.X;

public class MyAccount extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Column(name = X.ID)
  long                      id;

  @Column(name = "uid")
  long                      uid;

  @Column(name = "type")
  String                    type;

  @Column(name = "url")
  String                    url;

  @Column(name = "username")
  String                    username;

  @Column(name = "passwd")
  String                    password;

  @Column(name = "len")
  int                       len;
  
}
