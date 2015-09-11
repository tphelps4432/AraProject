package support_classes;

import android.content.Context;
import android.hardware.I2cManager;
import android.hardware.I2cTransaction;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tom Phelps on 8/24/2015.
 */
public class Comms {
    private static final String bus = "/dev/i2c-4";
    private static final I2cTransaction[] setupTxns = {
            WriteReg(0x00, 0x00, 0x00, 0x08), //AFE4400_CONTROL0

            WriteReg(0x01, 0x00, 0x17, 0xD4), //AFE4400_LED2STC
            WriteReg(0x02, 0x00, 0x1D, 0xAE), //AFE4400_LED2ENDC
            WriteReg(0x03, 0x00, 0x17, 0x70), //AFE4400_LED2LEDSTC
            WriteReg(0x04, 0x00, 0x1D, 0xAF), //AFE4400_LED2LEDENDC
            WriteReg(0x05, 0x00, 0x00, 0x00), //AFE4400_ALED2STC
            WriteReg(0x06, 0x00, 0x06, 0x3E), //AFE4400_ALED2ENDC

            WriteReg(0x07, 0x00, 0x08, 0x34), //AFE4400_LED1STC
            WriteReg(0x08, 0x00, 0x0E, 0x0E), //AFE4400_LED1ENDC
            WriteReg(0x09, 0x00, 0x07, 0xD0), //AFE4400_LED1LEDSTC
            WriteReg(0x0A, 0x00, 0x0E, 0x0F), //AFE4400_LED1LEDENDC
            WriteReg(0x0B, 0x00, 0x0F, 0xA0), //AFE4400_ALED1STC
            WriteReg(0x0C, 0x00, 0x15, 0xDE), //AFE4400_ALED1ENDC

            WriteReg(0x0D, 0x00, 0x00, 0x02), //AFE4400_LED2CONVST
            WriteReg(0x0E, 0x00, 0x07, 0xCF), //AFE4400_LED2CONVEND
            WriteReg(0x0F, 0x00, 0x07, 0xD2), //AFE4400_ALED2CONVST
            WriteReg(0x10, 0x00, 0x0F, 0x9F), //AFE4400_ALED2CONVEND

            WriteReg(0x11, 0x00, 0x0F, 0xA2), //AFE4400_LED1CONVST
            WriteReg(0x12, 0x00, 0x17, 0x6F), //AFE4400_LED1CONVEND
            WriteReg(0x13, 0x00, 0x17, 0x72), //AFE4400_ALED1CONVST
            WriteReg(0x14, 0x00, 0x1F, 0x3F), //AFE4400_ALED1CONVEND

            WriteReg(0x15, 0x00, 0x00, 0x00), //AFE4400_ADCRSTSTCT0
            WriteReg(0x16, 0x00, 0x00, 0x00), //AFE4400_ADCRSTENDCT0
            WriteReg(0x17, 0x00, 0x07, 0xD0), //AFE4400_ADCRSTSTCT1
            WriteReg(0x18, 0x00, 0x07, 0xD0), //AFE4400_ADCRSTENDCT1
            WriteReg(0x19, 0x00, 0x0F, 0xA0), //AFE4400_ADCRSTSTCT2
            WriteReg(0x1A, 0x00, 0x0F, 0xA0), //AFE4400_ADCRSTENDCT2
            WriteReg(0x1B, 0x00, 0x17, 0x70), //AFE4400_ADCRSTSTCT3
            WriteReg(0x1C, 0x00, 0x17, 0x70), //AFE4400_ADCRSTENDCT3

            WriteReg(0x1D, 0x00, 0x1F, 0x3F), //AFE4400_PRPCOUNT
            WriteReg(0x1E, 0x00, 0x01, 0x01), //AFE4400_CONTROL1
            WriteReg(0x20, 0x00, 0x00, 0x00), //AFE4400_TIAGAIN
            // Rf=100k Cf=50pF gain=0dB stage2=bypass ambdac=0µA
            WriteReg(0x21, 0x00, 0x00, 0x3A), //AFE4400_TIA_AMB_GAIN
            //WriteReg(0x22, 0x01, 0x14, 0x29), //AFE4400_LEDCNTRL
            WriteReg(0x22, 0x01, 0x4f, 0x4f), //AFE4400_LEDCNTRL
            WriteReg(0x23, 0x02, 0x01, 0x00), //AFE4400_CONTROL2
            WriteReg(0x29, 0x00, 0x00, 0x00), //AFE4400_ALARM

            // Setup SPI_READ
            WriteReg(0x00, 0x00, 0x00, 0x01), //AFE4400_CONTROL0
    };
    private static final I2cTransaction[] resetTxns = {
            WriteReg(0x00, 0x00, 0x00, 0x08), //AFE4400_CONTROL0
    };
    private static I2cManager i2c;
    private static I2cTransaction[] fetchInfo = null;
    private int address = (0x50 >> 1);
    private Context context;
    private android.os.Handler handler;
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public Comms(Context context, android.os.Handler handler) {
        this.context = context;
        this.i2c = (I2cManager) context.getSystemService("i2c");
        this.handler = handler;
    }

    private static I2cTransaction WriteReg(int reg, int b1, int b2, int b3) {
        return I2cTransaction.newWrite(0x01, reg, b1, b2, b3);
    }

    public void startChip() {
        execute(setupTxns);
    }

    public void stopChip() {
        execute(resetTxns);
    }


    public void setFetchInfo(int address, int reg, int b1, int b2, int b3) {
        this.address = address;
        fetchInfo = new I2cTransaction[]{
                WriteReg(reg,
                        b1, b2, b3),
                I2cTransaction.newRead(4),
        };
    }

    private I2cTransaction[] execute(I2cTransaction[] txns) {
        I2cTransaction[] results;
        try {
            results = null;
            for (I2cTransaction txn : txns)
                results = i2c.performTransactions(bus, address, txn);
        } catch (IOException e) {
            throw new RuntimeException("I2C error: " + e);
        }
        return results;
    }

    public void startCollection(int period, int delay, TimeUnit timeUnit) {
        if (fetchInfo == null) {
            System.out.println("NEED TO SET FETCH INFO FIRST");
        }
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                I2cTransaction[] results;
                byte[] data;
                results = execute(fetchInfo);
                data = results[0].data;
                handler.obtainMessage(0, data).sendToTarget();
            }
        }, delay, period, TimeUnit.MILLISECONDS);
    }

    public void startSingleCollection() {
        I2cTransaction[] results;
        byte[] data;
        results = execute(fetchInfo);
        int LED2VAL;
        data = results[0].data;
        LED2VAL = (((int) data[1] & 0xFF) << 16) |
                (((int) data[2] & 0xFF) << 8) |
                (((int) data[3] & 0xFF) << 0);
        if (LED2VAL >= 0x800000 && LED2VAL < 0x1000000)
            LED2VAL -= 0x1000000;

        handler.obtainMessage(0, LED2VAL, 0, data).sendToTarget();
    }

    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            assert false;
        }
    }
}

