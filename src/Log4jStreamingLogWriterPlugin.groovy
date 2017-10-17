import com.dtolabs.rundeck.core.logging.LogLevel
import com.dtolabs.rundeck.plugins.logging.StreamingLogWriterPlugin;

import com.dtolabs.rundeck.core.logging.LogEvent
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.log4j.MDC;

/**
 * This streaming log writer sends messages to log4j
 */
rundeckPlugin(StreamingLogWriterPlugin) {

    configuration {
        logger = "rundeck"
        logger required: true, description: "Logger instance name"


    }
    /**
     * The "open" closure is called to open the stream for writing events.
     * It is passed two map arguments, the execution data, and the plugin configuration data.
     *
     * It should return a Map containing the stream context, which will be passed back for later
     * calls to the "addEvent" closure.
     */
    open { Map execution, Map config ->
        // create the logger
        def Logger logger = Logger.getLogger(config.logger);

        // return context map for the plugin to reuse later
        [execution: execution, config: config, logger: logger]
    }

    /**
     * "addEvent" closure is called to append a new event to the stream.  
     * It is passed the Map of stream context created in the "open" closure, and a LogEvent.
     * Metadata is added to log event messages (see http://rundeck.org/docs/manual/job-workflows.html#context-variables)
     */
    addEvent { Map context, LogEvent event ->
        def Level level;
        switch (event.loglevel) {
            case LogLevel.ERROR:
                level = Level.ERROR
                break
            case LogLevel.WARN:
                level = Level.WARN
                break
            case LogLevel.VERBOSE:
            case LogLevel.DEBUG:
                level = Level.DEBUG
                break
            case LogLevel.NORMAL:
            case LogLevel.OTHER:
            default:
                level = Level.INFO
        }
        MDC.put("username", context.execution.username)
        MDC.put("name", context.execution.name)
        if (context.execution.group != null) {
            MDC.put("group", context.execution.group)
        }
        MDC.put("execid", context.execution.execid)
        MDC.put("project", context.execution.project)
        MDC.put("id", context.execution.id)
        MDC.put("url", context.execution.url)
        MDC.put("loglevel", context.execution.loglevel)

        context.logger.log(level, event.message)

    }

    /**
     * "close" closure is called to end writing to the stream.
     */
    close { Map context ->

    }
}
