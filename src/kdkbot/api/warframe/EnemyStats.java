package kdkbot.api.warframe;

import java.lang.reflect.Field;
import java.time.Instant;

public class EnemyStats {
	public String name;
	public String faction;
	public String planet;
	public String mission;
	public String weapons;
	public String abilities;
	public String health;
	public String healthType;
	public String shield;
	public String shieldType;
	public String armor;
	public String armorType;
	public String baseAffinity;
	public String baseLevel;
	public String codexScans;
	public String modDrops;
	public String otherDrops;
	
	
	public void debugPrintData() {
		Field[] fields = this.getClass().getFields();
		try {
			for (Field field : fields) {
				System.out.println(field.getName() + ": " + field.get(this));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Class<?> self = null;
		try {
			// Expiration Date (now + 15 days)
			sb.append("expirationDate=" + (Instant.now().toEpochMilli() + (1000 * 60 * 60 * 24 * 15))); // 15 days
			
			// Set values
			self = this.getClass();
			
			Field[] fields = self.getDeclaredFields();
			for(Field field : fields) {
				String fieldName = field.getName();
				String fieldValue = field.get(this).toString();
				
				sb.append(fieldName + "=" + fieldValue + "\r\n");
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
}
