package au.com.gamingutils.jeli.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EventPriority {

    HIGHEST('H'),
    HIGH('h'),
    NORMAL('N'),
    LOW('l'),
    LOWEST('L'),
    COMPLETE('C')
    ;
    private final char marker;
}
