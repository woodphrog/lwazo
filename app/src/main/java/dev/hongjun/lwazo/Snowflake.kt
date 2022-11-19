package dev.hongjun.lwazo

import de.mkammerer.snowflakeid.*

val snowflakeIdGenerator: SnowflakeIdGenerator = SnowflakeIdGenerator.createDefault(0);

fun generateSnowflakeId(): Long {
    return snowflakeIdGenerator.next()
}
