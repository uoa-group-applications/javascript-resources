package nz.ac.auckland.common.jsresource;

/**
 * Author: Marnix
 * <p/>
 * An application resource has a scope in which it will operate
 */
public enum ResourceScope {
  /**
   * Global scope is used when application resources should be available to anyone
   */
  Global,

  /**
   * Session scope contains resources specific to a user
   */
  Session,

  /**
   * State to cope with unparseable scope identifier
   */
  Unknown
}
