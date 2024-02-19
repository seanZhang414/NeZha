package cn.com.duiba.nezha.compute.biz.utils.conf;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfProperties extends Properties{


    private static final long serialVersionUID = 1L;

    protected ConfProperties(final String filePath) throws FileNotFoundException {
        final InputStream inStream = ConfProperties.class.getResourceAsStream(filePath);
        try {
            load(inStream);
        } catch (final IOException e) {
            e.printStackTrace();
            throw new NullPointerException("Failed to load config file: " +
                    filePath + ", error: " + e.getMessage());
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (final IOException e) {
                    // do nothing
                }
            }

        }
    }

    public int getInt(final String propertyName, final int defaultValue) {
        int propertyValue = defaultValue;

        final String valueStr = getProperty(propertyName);
        try {
            propertyValue = Integer.parseInt(valueStr);
        } catch (final Exception e) {
            // do nothing, just return the default value;
        }

        return propertyValue;
    }

    public float getFloat(final String propertyName, final float defaultValue) {
        float propertyValue = defaultValue;

        final String valueStr = getProperty(propertyName);
        try {
            propertyValue = Float.parseFloat(valueStr);
        } catch (final Exception e) {
            // do nothing, just return the default value;
        }

        return propertyValue;
    }

    public double getDouble(final String propertyName, final double defaultValue) {
        double propertyValue = defaultValue;

        final String valueStr = getProperty(propertyName);
        try {
            propertyValue = Double.parseDouble(valueStr);
        } catch (final Exception e) {
            // do nothing, just return the default value;
        }

        return propertyValue;
    }

}

