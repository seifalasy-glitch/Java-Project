# Attendance Management System - COMP 301

مشروع Java + Swing لإدارة حضور الطلبة (Lecture / Lab / Section).

## التشغيل
0. لو فيه ملف `AttendanceSystem.iml` امسحه (قديم).
1. IntelliJ: File > Open > اختار فولدر `Program` ده.
2. لو طلب SDK اختار JDK `27` (نفس بتاع الشيتات).
3. لو فولدر `src` مش أزرق: كليك يمين > Mark Directory as > Sources Root.
4. شغّل Run configuration اسمها **Attendance System** (أو افتح `src/Main.java` ودوس Run).

| الدور | Email | Password |
|---|---|---|
| Admin | admin@ams.com | admin123 |
| Teacher | hany@ams.com | 1234 |
| Teacher | ahmed@ams.com | 1234 |

البيانات بتتحفظ في فولدر `data` (بيتعمل لوحده أول تشغيل). امسحه عشان تبدأ من الأول.
مفيش أي مكتبات خارجية ولا حاجة محتاجة تتربط يدوي.

## الملفات
- `docs/UML_Design_EN.pdf` : الـ UML والشرح الأكاديمي بالإنجليزي (Milestone 1).
- `docs/Project_Explained_AR.pdf` : شرح كل كلاس وكل حاجة بالعربي.
- `docs/Technologies_and_Setup_AR.pdf` : التقنيات المستخدمة وطريقة التشغيل.
- `docs/uml/` : كل diagram كـ SVG (تكبير من غير ما يبوظ) و PNG.
- `src/model` الكلاسات، `src/data` الحفظ في الملفات وتوليد الـ IDs، `src/gui` الشاشات.
