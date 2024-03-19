import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String path = "G://1A/ip_addresses";
        IPV4Counter allByteArray = new IPV4Counter();
        allByteArray.StreamHH(path);
    }
}

class IPV4Counter {
    byte[] arr = new byte[536870913];
    CountIPV4 ipv4 = new CountIPV4(arr);

    public void StreamHH(String path) {
        long m = System.currentTimeMillis();
        try (Stream<String> lines = java.nio.file.Files.lines(Paths.get(path))) {
            lines.forEach(ipv4::checkLine);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ipv4.sincher.addIP();
        System.out.println("ВРЕМЯ: " + (double) (System.currentTimeMillis() - m) / 1000);
        System.out.println("Количество уникальных адресов: " + countArr());
    }

    private long countArr() {
        long totalIP = 0;
        for (int i = 0; i < 536870913; i++) {
            for (int j = 0; j < 8; j++) {
                if (getBit(arr[i], j))
                    totalIP++;
            }
        }
        return totalIP;
    }

    private boolean getBit(byte _byte, int bitPosition) {
        return (_byte & (1 << bitPosition)) != 0;
    }
}

class CountIPV4 {
    byte[] arr;

    CountIPV4(byte[] arr) {
        this.arr = arr;
        sincher = new Sincher(this.arr);
    }

    int[] ip = new int[4];
    char[] tempCharNum = new char[3];
    byte splitNum, chartNum;
    char charNow;
    int tempNumer, lineSize;
    int MIN_48 = 48;
    Sincher sincher;

    public void checkLine(String line) {
        splitStr(line);
        sincher.addToArr(ip[0], ip[1], ip[2], ip[3]);
    }

    private void splitStr(String line) {
        lineSize = line.length();
        for (int i = 0; i < lineSize; i++) {
            charNow = line.charAt(i);
            if (charNow != '.') {
                tempCharNum[chartNum] = charNow;
                chartNum++;
            } else {
                toIPArr();
            }
        }
        toIPArr();
        splitNum = 0;
    }

    private void toIPArr() {
        if (chartNum == 3) {
            tempNumer = (tempCharNum[0] - MIN_48) * 100 + (tempCharNum[1] - MIN_48) * 10 + (tempCharNum[2] - MIN_48);
        } else if (chartNum == 2) {
            tempNumer = (tempCharNum[0] - MIN_48) * 10 + (tempCharNum[1] - MIN_48);
        } else {
            tempNumer = tempCharNum[0] - MIN_48;
        }
        ip[splitNum] = tempNumer;
        splitNum++;
        chartNum = 0;
    }
}

class Sincher {
    Sincher(byte[] arr) {
        this.arr = arr;
    }

    byte[] arr;
    int arrayAlloc = 10_000_000;
    int zg = 0;
    int[] ipArr = new int[arrayAlloc];

    public void addToArr(int ip1, int ip2, int ip3, int ip4) {
        int addr = ipV4ToInt(ip1, ip2, ip3, ip4);
        ipArr[zg] = addr;
        zg++;
        if (zg == arrayAlloc) {
            addIP();
            zg = 0;
        }
    }

    void addIP() {
        try (IntStream integerStream = Arrays.stream(ipArr)) {
            integerStream.parallel().forEach(this::toArrayIp);
        }
    }

    private void toArrayIp(int addr) {
        long chunkL = ((long) addr + 2147483647) / 8;
        long bitNumL = ((long) addr + 2147483647) % 8;
        setChunkBit((int) chunkL, (byte) bitNumL);
    }

    private void setChunkBit(int _byte, byte bitPosition) {
        arr[_byte] = (byte) (arr[_byte] | (1 << bitPosition));
    }

    private int ipV4ToInt(int a1, int a2, int a3, int a4) {
        return (a1 << 24) ^ (a2 << 16) ^ (a3 << 8) ^ a4;
    }
}



