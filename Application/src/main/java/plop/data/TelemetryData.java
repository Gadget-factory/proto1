package plop.data;

import com.example.android.common.logger.Log;

/**
 * Data logged from uC
 *
 123,-456,78,90,123,456,-7890,1
 123 => 12.3 V
 -456 => -45.6 A
 78 => 78°C sensor 1 (mosfets)
 90 => 90°C sensor 2 (battery)
 123 => 12.3°
 456 => 4.56 rps (revolutions per second)
 -7890 => -7890 WS (energy in watts.seconds , '-' => braking)
 1 => buzzer = ON (0=> buzzer = Off)
 */
public class TelemetryData implements LoggableData {

    private static final String SEPARATOR = ",";

    private String logEntry;

    //Values as fixed point numbers

    private int voltage;  //10th
    private int current;  //10th
    private int boardTemperature;
    private int batteryTemperature;
    private int inclination;  //10th
    private int rps;  //100th
    private int wattSeconds;
    private boolean buzzer;

    public TelemetryData(String logEntry)
    {
        this.logEntry = logEntry;

        String[] values = logEntry.split(SEPARATOR);

        if(values.length != 8)
        {
            Log.w("TelemetryData", "Invalid data, didn't find all values");
            return;
        }

        voltage = Integer.parseInt(values[0]);
        current = Integer.parseInt(values[1]);
        boardTemperature = Integer.parseInt(values[2]);
        batteryTemperature = Integer.parseInt(values[3]);
        inclination = Integer.parseInt(values[4]);
        rps = Integer.parseInt(values[5]);
        wattSeconds = Integer.parseInt(values[6]);
        buzzer = values[7].startsWith("1");
    }

    public TelemetryData(String logEntry, int voltage, int current, int boardTemperature, int batteryTemperature, int inclination, int rps, int wattSeconds, boolean buzzer) {
        this.logEntry = logEntry;
        this.voltage = voltage;
        this.current = current;
        this.boardTemperature = boardTemperature;
        this.batteryTemperature = batteryTemperature;
        this.inclination = inclination;
        this.rps = rps;
        this.wattSeconds = wattSeconds;
        this.buzzer = buzzer;
    }

    public boolean getBuzzer() {
        return buzzer;
    }

    public int getWattSeconds() {
        return wattSeconds;
    }

    public int getRps() {
        return rps;
    }

    public int getInclination() {
        return inclination;
    }

    public int getBatteryTemperature() {
        return batteryTemperature;
    }

    public int getBoardTemperature() {
        return boardTemperature;
    }

    public int getCurrent() {
        return current;
    }

    public int getVoltage() {
        return voltage;
    }

    @Override
    public LogDataType getLogDataType() {
        return LogDataType.STRING;
    }

    @Override
    public String getLogEntry() {
        return logEntry;
    }

    @Override
    public byte[] getLogData() {
        return new byte[0];
    }

    public String getVoltageString()
    {
        return getString(voltage, 1);
    }

    public String getCurrentString()
    {
        return getString(current, 1);
    }

    public String getInclinationString()
    {
        return getString(inclination, 1);
    }

    public String getRPSString()
    {
        return getString(rps, 2);
    }

    private String getString(int value, int decimals)
    {
        char[] numberStr = Integer.toString(value).toCharArray();
        StringBuilder str = new StringBuilder(8);

        if(numberStr.length <= decimals)
        {
            str.append(0);
        }
        else {
            for (int i = 0; i < numberStr.length - decimals; i++) {
                str.append(numberStr[i]);
            }
        }

        str.append(".");

        int startIndex = numberStr.length-decimals;
        if(startIndex >= 0) {
            for (int i = startIndex; i < numberStr.length; i++) {
                str.append(numberStr[i]);
            }
        }
        else
        {
            for(int i = startIndex; i < 0; i++)
            {
                str.append("0");
            }
            for(int i = 0; i < numberStr.length; i++)
            {
                str.append(numberStr[i]);
            }
        }

        return str.toString();
    }
}
