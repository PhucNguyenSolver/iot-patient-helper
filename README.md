# HỆ THỐNG THÔNG MINH HỖ TRỢ HỌC SINH TIỂU HỌC

- Hệ Thống Hỗ Trợ Học Sinh Thông Minh giúp nhà trường phản ứng nhanh với các sự cố trong lớp học. Hệ thống gồm nút báo động, cảm biến, camera và một ứng dụng di động. Khi có báo động, ứng dụng ngay lập tức gửi thông báo kèm hình ảnh camera trực tiếp đến giáo viên, nhân viên y tế để xử lý kịp thời. Đây là giải pháp đơn giản giúp môi trường học đường thông minh và an toàn hơn. 

## Yêu cầu chi tiết phần App
- App di động kết nối đến máy chủ MQTT ✔️
- có thể cấu hình được đường link server máy chủ ❌
- khi nhận được gói tin, app sẽ cảnh báo cho người dùng bằng cách đổ chuông và hiện thông báo ✔️
- khi người dùng click vào thông báo, app sẽ mở lên và hiện camera phòng học (camera realtime) ✔️
- có thể cấu hình được link camera ❌
- người dùng bấm nút xác nhận để gửi data lên máy chủ ✔️ (để những user khác biết rằng trường hợp này đã được tiếp nhận xử lý)

*❌: chưa hoàn thành
Đang fix cứng link máy chủ - máy chủ mosquitto (test.mosquitto.org). Đề xuất dùng luôn máy chủ này khi setup thiết bị phần cứng
Do hạn chế không hiển thị được RTSP -> giải pháp tạm thời là dựng server trung gian chuyển đổi RTSP về dạng HTTP hiển thị được trên web, sau đó hiển thị web này trên màn hình app. Hiện tại chỉ có 1 link camera để test và chưa cấu hình được nhiều link.


## Kết quả dự án
- [APK file](./builds/app-debug.apk) | Last updated: 24/03/2025
- [web mô phỏng thiết bị hỗ trợ](https://rtsp.gasbinhminh.vn/hardware) | [mã nguồn](https://github.com/PhucNguyenSolver/iot-patient-helper-web)
- [video demo test](https://drive.google.com/file/d/1Hj2qO_p4T0CnvsIfxnotYS-5d10WGU2r/view?usp=drive_link)
