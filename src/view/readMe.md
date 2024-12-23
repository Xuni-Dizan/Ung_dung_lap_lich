# Ứng dụng Lịch Công việc Cá nhân

## Giới thiệu

Ứng dụng Lịch Công việc Cá nhân là một ứng dụng Java Swing được thiết kế để giúp người dùng quản lý công việc hàng ngày và theo dõi lịch trình của họ. Ứng dụng cung cấp giao diện trực quan và thân thiện với người dùng để tạo, chỉnh sửa, và theo dõi tiến độ các công việc.

## Tính năng chính

* Hiển thị lịch: Hiển thị lịch tháng với các ngày được đánh dấu rõ ràng, cho phép người dùng dễ dàng điều hướng giữa các ngày, tháng.
* Quản lý công việc: Người dùng có thể tạo, chỉnh sửa và xóa công việc.
* Giao diện tab: Cho phép người dùng tạo nhiều tab để quản lý công việc theo các danh mục khác nhau (ví dụ: Công việc, Học tập, Cá nhân).
* Lưu trữ dữ liệu: Dữ liệu công việc được lưu trữ vào tệp để người dùng có thể truy cập lại sau khi đóng ứng dụng.
* Tùy chỉnh giao diện: Hỗ trợ chế độ sáng/tối để phù hợp với sở thích của người dùng.

## **Tính Năng Nổi Bật**

- **Giao Diện Thân Thiện**: Thiết kế giao diện hiện đại, dễ sử dụng với chế độ Light Mode và Dark Mode.
- **Quản Lý Công Việc**: Thêm, chỉnh sửa, và xóa các công việc hàng ngày với thông tin chi tiết như tên công việc, thời gian bắt đầu, thời gian kết thúc và trạng thái.
- **Đa Tab**: Hỗ trợ nhiều tab để quản lý các lịch khác nhau, giúp người dùng tổ chức công việc theo nhiều danh mục.
- **Lưu Trữ Dữ Liệu**: Dữ liệu công việc được lưu trữ cục bộ, đảm bảo an toàn và dễ dàng truy cập khi cần thiết.
- **Thông Báo**: Cài đặt thời gian để nhận thông báo về các công việc sắp tới.
- **Chuyển Đổi Ngày Tháng**: Dễ dàng di chuyển giữa các tháng và ngày bằng các nút điều hướng.

## **Yêu Cầu Hệ Thống**

- **Hệ Điều Hành**: Hỗ trợ Windows, macOS, và Linux.
- **Java**: Phiên bản Java 8 trở lên.
- **Thư Viện**: 
  - [JCalendar](https://toedter.com/jcalendar/) để chọn ngày tháng.
  
## **Cài Đặt**

1. **Tải Dự Án**

   Tải toàn bộ mã nguồn từ kho lưu trữ của chúng tôi:

   ```bash
   git clone https://github.com/your-repo/calendar-app.git
   ```

2. **Cài Đặt Java**

   Đảm bảo rằng Java đã được cài đặt trên máy tính của bạn. Bạn có thể tải Java từ [trang chủ Oracle](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html).

3. **Cài Đặt Thư Viện Phụ Thuộc**

   Sử dụng Maven hoặc Gradle để quản lý các thư viện phụ thuộc. Ví dụ với Maven, thêm vào `pom.xml`:

   ```xml
   <dependency>
       <groupId>com.toedter</groupId>
       <artifactId>jcalendar</artifactId>
       <version>1.4</version>
   </dependency>
   ```

4. **Biên Dịch và Chạy Ứng Dụng**

   Sử dụng lệnh sau để biên dịch và chạy ứng dụng:

   ```bash
   mvn clean install
   java -jar target/calendar-app.jar
   ```

## **Cách Sử Dụng**

1. **Mở Ứng Dụng**

   Khởi chạy ứng dụng để truy cập giao diện chính.

2. **Thêm Công Việc Mới**

   - Nhấp vào nút "Add Task" để mở hộp thoại thêm công việc mới.
   - Nhập đầy đủ thông tin công việc như tên, thời gian bắt đầu, thời gian kết thúc và trạng thái.
   - Nhấn "OK" để lưu công việc.

3. **Chỉnh Sửa và Xóa Công Việc**

   - Trong bảng danh sách công việc, nhấp vào nút "Edit" để chỉnh sửa thông tin công việc.
   - Nhấp vào nút "Delete" để xóa công việc khỏi danh sách.

4. **Quản Lý Các Tab**

   - Nhấp vào nút "+" để thêm một tab mới với tiêu đề tùy chọn.
   - Sử dụng các tab để phân loại và quản lý công việc theo nhu cầu.

5. **Chuyển Đổi Tháng và Ngày**

   - Sử dụng các nút "Tháng trước" và "Tháng sau" để di chuyển giữa các tháng.
   - Nhấp vào ngày cụ thể trên lịch để xem và quản lý công việc cho ngày đó.

6. **Chế Độ Dark/Light**

   - Tùy chỉnh giao diện giữa Dark Mode và Light Mode để có trải nghiệm sử dụng tối ưu.


## Cấu trúc dự án

Dự án được tổ chức thành các lớp sau:

hãy viết mô tả đầy đủ, chi tiết và chuyên nghiệp nhất về dự án này vào tệp readMe.md

ung_dung_lap_lich/
│
├── src/
│ ├── controller/
│ ├── model/
│ ├── test/
│ ├── view/
│ │ ├── CalendarEventHandler.java
│ │ ├── TodayPanel.java
│ │ ├── Utilities.java
│ │ ├── CalendarGUI.java
│ │ ├── Main.java
│ │ ├── DailyPlan.java
│ │ ├── TaskManager.java
│ │ ├── TabInfo.java
│ │ ├── Task.java
│ │ └── readMe.md
│ └── resources/
│ ├── fonts/
│ │ └── Roboto-Regular.ttf
│ └── images/
│ └── logo.png
│
├── tasks_data.dat
├── tabs_data.dat
├── pom.xml
└── README.md

- **CalendarEventHandler.java**: Xử lý các sự kiện liên quan đến lịch, như cập nhật lịch khi chọn ngày mới.
- **TodayPanel.java**: Panel hiển thị các công việc của ngày hiện tại.
- **Utilities.java**: Các phương thức tiện ích để áp dụng kiểu dáng và thao tác với giao diện.
- **CalendarGUI.java**: Giao diện chính của ứng dụng lịch, bao gồm các thành phần giao diện và chức năng điều hướng.
- **Main.java**: Điểm khởi đầu của ứng dụng.
- **DailyPlan.java**: Quản lý các tab và kế hoạch hàng ngày.
- **TaskManager.java**: Quản lý dữ liệu công việc, bao gồm thêm, sửa, xóa và lưu trữ.
- **TabInfo.java**: Thông tin về mỗi tab, bao gồm tiêu đề và ngày được chọn.
- **Task.java**: Mô hình dữ liệu cho công việc.

## Hướng dẫn sử dụng

1. Chạy ứng dụng bằng cách thực thi lớp `Main`.
2. Chọn ngày trên lịch hoặc sử dụng các nút điều hướng để chuyển đổi giữa các ngày.
3. Tạo tab mới để quản lý các danh mục công việc khác nhau.
4. Thêm, chỉnh sửa, hoặc xóa công việc trong mỗi tab.
5. Đóng ứng dụng để lưu dữ liệu công việc.

## Công nghệ sử dụng

* Java Swing:  Framework để xây dựng giao diện người dùng.
* JCalendar: Thư viện để hiển thị lịch.

## **Đóng Góp**

Chúng tôi hoan nghênh mọi đóng góp từ cộng đồng! Để đóng góp vào dự án, hãy thực hiện theo các bước sau:

1. **Fork Kho Lưu Trữ**

   Nhấp vào nút "Fork" ở góc trên bên phải của trang kho lưu trữ.

2. **Clone Kho Lưu Trữ Forked**

   ```bash
   git clone https://github.com/Xuni-Dizan/Ung_dung_lap_lich.git
   ```

3. **Tạo Nhánh Mới**

   ```bash
   git checkout -b feature/YourFeatureName
   ```

4. **Thực Hiện Thay Đổi và Commit**

   Thực hiện các thay đổi bạn muốn và commit chúng:

   ```bash
   git commit -m "Add Your Feature"
   ```

5. **Đẩy Thay Đổi Lên GitHub**

   ```bash
   git push origin feature/YourFeatureName
   ```

6. **Tạo Pull Request**

   Vào kho lưu trữ gốc của dự án và tạo một Pull Request để chúng tôi xem xét.

## **Liên Hệ**

Nếu bạn có bất kỳ câu hỏi hoặc đóng góp nào, vui lòng liên hệ với chúng tôi:

- **Email**: xunidizan@gmail.com
- **GitHub**: [https://github.com/Xuni-Dizan/Ung_dung_lap_lich](https://github.com/your-repo/calendar-app)
- **Telegram**: [@your_telegram](https://t.me/your_telegram)

---

© 2024 **Ung_dung_lap_lich**. Bảo lưu mọi quyền.

##  Mở rộng trong tương lai

* Cải thiện giao diện người dùng.
* Bổ sung tính năng nhắc nhở công việc.
* Đồng bộ hóa dữ liệu với các dịch vụ đám mây.
* Hỗ trợ nhiều ngôn ngữ.