package org.drkswg;

import org.drkswg.exception.GetUniqueLinesCountException;

public interface UniqueCounter {
    long getUniquesCount() throws GetUniqueLinesCountException;
}
