package cn.com.duiba.nezha.engine.deploy.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

import org.springframework.stereotype.Component;

/**
 * Created by xuezhaoming on 16/5/24.
 */
@Component()
public class DruidFilter implements Filter {

    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}
