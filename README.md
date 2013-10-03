## Description

This StreamingLogWriter plugin sends job output messages to the specified
log4j logger. This is useful if you want to direct the job output
messages to a particular destination via an [appender](http://logging.apache.org/log4j/2.x/manual/appenders.html)
(eg, SyslogAppender).

## Deploy

This is a groovy plugin so just copy the .groovy file to $RDECK_BASE/libext.

## Configuration

To enable the plugin, update the `rundeck-config.properties` file and 
declare the "rundeck.execution.logs.streamingWriterPlugins" property.
If the property already exists use commas to separate each plugin name.

Example: rundeck-config.properties

    rundeck.execution.logs.streamingWriterPlugins=Log4jStreamingLogWriterPlugin


You can name the log4j logger anything you wish. By default, it is named "rundeck".
To override the default name, update the `project.properties` file with the following entry:

    project.plugin.StreamingLogWriter.Log4jStreamingLogWriterPlugin.logger=my-logger-name

You can see the logger was named "my-logger-name".


## Usage

Edit rundeck's log4j.properties to set up the appender you wish to send the log messages.

Here is MDC data available for use in the log4j conversion pattern layouts.

* username: User that ran the job
* project: Project name
* name: the job name
* group: the job group
* id: the job ID
* execid: the execution ID
* url: The URL to the execution page
* loglevel: the log event log level

Use the `%X` format specifier with one of the properties. Eg

     %X{username} %X{project}:%X{group}/%X{name} %X{execid}

## Example

Here's an example that sends messages to syslog. The logger name is the default "rundeck"
but change that to whatever you specified earlier.

Example: log4j.properties

```
log4j.rundeck=INFO, SYSLOG
# configure Syslog facility LOCAL1 appender
log4j.appender.SYSLOG=org.apache.log4j.net.SyslogAppender
log4j.appender.SYSLOG.threshold=WARN
log4j.appender.SYSLOG.syslogHost=localhost
log4j.appender.SYSLOG.facility=LOCAL4
log4j.appender.SYSLOG.layout=org.apache.log4j.PatternLayout
log4j.appender.SYSLOG.layout.ConversionPattern=%d{ISO8601} %-5p %c{1} - %X{username} %X{project} %X{group}/%X{name} #%X{execid} %X{url} - %m%n
```
Here's an example log message using the layout above:

    Oct  2 23:18:03 2013-10-02 23: 18:03,150 INFO - rundeck admin anvils nightly_catalog_rebuild #5 http://192.168.50.2:4440/execution/follow/5 - Completed.

If you are running rsyslog on Linux, be sure the rsyslog.conf enabled the
udp/tcp input. eg:
```
# Provides UDP syslog reception
$ModLoad imudp
$UDPServerRun 514

# Provides TCP syslog reception
$ModLoad imtcp
$InputTCPServerRun 514
```
