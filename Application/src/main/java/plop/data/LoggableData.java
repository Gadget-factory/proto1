package plop.data;

/**
 * Simple interface for loggable data
 */
public interface LoggableData
{

    public enum LogDataType
    {
        BINARY,
        STRING
    }

    ;

    /**
     * Returns the data type this LoggableData constains
     *
     * @return Data type see LogDataType -enumeration
     */
    LogDataType getLogDataType();

    /**
     * Returns the log string
     *
     * @return Log string
     */
    String getLogEntry();

    /**
     * Returns the binary data to log
     *
     * @return byte-array
     */
    byte[] getLogData();
}
