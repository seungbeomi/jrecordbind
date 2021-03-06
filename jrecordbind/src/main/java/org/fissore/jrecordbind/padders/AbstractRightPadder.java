/*
 * JRecordBind, fixed-length file (un)marshaller
 * Copyright 2019, Federico Fissore, and individual contributors. See
 * AUTHORS.txt in the distribution for a full listing of individual
 * contributors.
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

package org.fissore.jrecordbind.padders;

public abstract class AbstractRightPadder extends AbstractPadder {

  public AbstractRightPadder(char padChar) {
    super(padChar);
  }

  public String pad(String input, int totalLength) {
    if (input == null) {
      return buildPad(null, totalLength);
    }
    return input + buildPad(input, totalLength);
  }

  public String unpad(String input) {
    if (input == null || input.isEmpty()) {
      return "";
    }

    int inputIdx = input.length() - 1;
    while (inputIdx >= 0 && input.charAt(inputIdx) == padChar) {
      inputIdx--;
    }

    return input.substring(0, inputIdx + 1);
  }
}
