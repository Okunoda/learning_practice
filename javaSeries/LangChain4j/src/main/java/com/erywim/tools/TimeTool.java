package com.erywim.tools;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

/**
 * @Date 2026/1/3
 */

public class TimeTool {

    @Tool(value = "获取当前实时的时间，当用户提问中涉及到询问时间时使用该工具", name = "get_time")
    public String getTime(@P("时区信息") String timeZone,@P("用户的提问内容")String question) {
        System.out.println("工具被调用-" + question + "-" + timeZone);

        return "The time in " + timeZone + " is " + java.time.ZonedDateTime.now(java.time.ZoneId.of(timeZone)).toLocalTime();
    }

}
