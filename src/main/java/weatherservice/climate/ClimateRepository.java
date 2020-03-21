package weatherservice.climate;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public class ClimateRepository {

  private final ReadLock readLock;
  private final WriteLock writeLock;
  private ClimateDto climate;

  public ClimateRepository() {
    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    this.readLock = lock.readLock();
    this.writeLock = lock.writeLock();

    ClimateDto climate = new ClimateDto();
    climate.setTemperature(10);
    climate.setHumidity(80);
    this.climate = climate;
  }

  public ClimateDto get() {
    readLock.lock();
    try {
      ClimateDto climateCopy = new ClimateDto();
      climateCopy.setTemperature(climate.getTemperature());
      climateCopy.setHumidity(climate.getHumidity());
      return climateCopy;
    } finally {
      readLock.unlock();
    }
  }

  public void save(ClimateDto climate) {
    writeLock.lock();
    try {
      this.climate = climate;
    } finally {
      writeLock.unlock();
    }
  }
}
