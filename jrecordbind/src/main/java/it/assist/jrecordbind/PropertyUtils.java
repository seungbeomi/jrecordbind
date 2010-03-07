/*
 * JRecordBind, fixed-length file (un)marshaller
 * Copyright 2009, Assist s.r.l., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package it.assist.jrecordbind;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

class PropertyUtils {

  public static interface Getter<E, O> {

    public O get(E o);

  }

  static final class ReadMethodGetter implements Getter<PropertyDescriptor, Method> {

    public Method get(PropertyDescriptor p) {
      return p.getReadMethod();
    }
  }

  static final class WriteMethodGetter implements Getter<PropertyDescriptor, Method> {

    public Method get(PropertyDescriptor p) {
      return p.getWriteMethod();
    }
  }

  private final Logger log = Logger.getLogger(PropertyUtils.class.getName());

  private final Map<Class<?>, Map<String, Method>> getters;
  private final Map<Class<?>, Map<String, Method>> setters;

  public PropertyUtils() {
    this.getters = new HashMap<Class<?>, Map<String, Method>>();
    this.setters = new HashMap<Class<?>, Map<String, Method>>();
  }

  Object getProperty(Object record, String name) {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Getting property " + name + " from object of " + record.getClass());
    }
    try {
      Class<? extends Object> clazz = record.getClass();
      if (!getters.containsKey(clazz)) {
        populateMethodsMap(clazz, getters, new ReadMethodGetter());
      }
      return getters.get(clazz).get(name).invoke(record);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  private void populateMethodsMap(Class<?> clazz, Map<Class<?>, Map<String, Method>> methodsMap,
      Getter<PropertyDescriptor, Method> getter) throws IntrospectionException {
    HashMap<String, Method> methods = new HashMap<String, Method>();
    PropertyDescriptor[] descriptors = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
    for (PropertyDescriptor p : descriptors) {
      methods.put(p.getName(), getter.get(p));
    }
    methodsMap.put(clazz, methods);
  }

  void setProperty(Object record, String name, Object value) {
    if (log.isLoggable(Level.FINE)) {
      log.log(Level.FINE, "Setting property " + name + " with value '" + value + "' to object of " + record.getClass());
    }
    try {
      Class<? extends Object> clazz = record.getClass();
      if (!setters.containsKey(clazz)) {
        populateMethodsMap(clazz, setters, new WriteMethodGetter());
      }
      setters.get(clazz).get(name).invoke(record, value);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

}
