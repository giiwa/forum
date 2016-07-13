package org.giiwa.forum.web.forum;

import org.giiwa.framework.web.Model;
import org.giiwa.framework.web.Path;

/**
 * web api: /demo
 * 
 * @author joe
 * 
 */
public class index extends Model {

  @Path(login = true)
  public void home() {
    this.redirect("/forum/circling");
  }

}
