# 📍 GPS Plugin by Meo

A Minecraft plugin for creating and navigating custom GPS routes in-game.

🌐 Website: [meo.pp.ua](https://meo.pp.ua)  
📥 Download Latest: [Click here to download](https://github.com/Thanawatttt/Gps-Plugin/releases/)

---

## 📌 คำสั่ง / Commands

### 🔧 จัดการเส้นทาง / Route Management

- `/gps create <ชื่อ>` – สร้างเส้นทางใหม่ / Create a new route  
- `/gps add <ชื่อ>` – เพิ่มจุดในเส้นทาง (เฉพาะในเกม) / Add a waypoint (in-game only)  
- `/gps removepoint <ชื่อ> <index>` – ลบจุดตามลำดับ / Remove a waypoint by index  
- `/gps remove <ชื่อ>` – ลบเส้นทาง / Delete the route

### 🧭 นำทาง / Navigation

- `/gps list` – ดูรายการเส้นทางทั้งหมด / List all GPS routes  
- `/gps start <ชื่อ> [player]` – เริ่มนำทาง (สามารถใส่ชื่อผู้เล่นจาก console ได้)  
  Start navigation (optionally specify player from console)  
- `/gps stop` – หยุดการนำทาง (เฉพาะในเกม) / Stop navigation (in-game only)

---

## ▶️ ตัวอย่าง / Example

```yaml
/gps create village_path
/gps add village_path
/gps add village_path
/gps list
/gps start village_path
