/*

    ngs-fca  Formal concept analysis for genomics.
    Copyright (c) 2014-2015 National Marrow Donor Program (NMDP)

    This library is free software; you can redistribute it and/or modify it
    under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation; either version 3 of the License, or (at
    your option) any later version.

    This library is distributed in the hope that it will be useful, but WITHOUT
    ANY WARRANTY; with out even the implied warranty of MERCHANTABILITY or
    FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
    License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this library;  if not, write to the Free Software Foundation,
    Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307  USA.

    > http://www.gnu.org/licenses/lgpl.html

*/
package org.nmdp.ngs.fca;

public final class TotallyOrdered<T extends TotallyOrdered> extends Binary<T> implements Comparable<T> {
    
    @Override
    public boolean apply(final T that) {
        return this.compareTo(that) == 0;
    }
    
    @Override
    public boolean isLessOrEqualTo(final T that) {
        return this.isLessThan(that) || this.apply(that);
    }
    
    @Override
    public boolean isLessThan(final T that) {
        return this.compareTo(that) == -1;
    }
    
    @Override
    public boolean isGreaterOrEqualTo(final T that) {
        return this.isGreaterThan(that) || this.apply(that);
    }
    
    @Override
    public boolean isGreaterThan(final T that) {
        return this.compareTo(that) == 1;
    }

    @Override
    public int compareTo(final T that) {
        return this.compareTo(that);
    }
}