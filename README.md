# MultiCmd

Fabric client mod cho Minecraft 1.21.4. Bấm phím (mặc định `R`) để tự chạy lần lượt:

1. `/delhome 2`
2. `/sethome 2`
3. `/rtp`
4. `/home 2`

Bấm phím lần nữa khi đang chạy để huỷ. Trạng thái hiện trên action bar.

## Cấu hình

File `.minecraft/config/multicmd.json` (tự tạo lần đầu chạy, sửa xong bấm phím là áp dụng luôn, không cần restart):

```json
{
  "steps": [
    { "cmd": "delhome 2", "wait": 10 },
    { "cmd": "sethome 2", "wait": 10 },
    { "cmd": "rtp", "wait": 30 },
    { "cmd": "home 2", "wait": 0 }
  ]
}
```

- `cmd`: lệnh, có hay không có dấu `/` đều được.
- `wait`: số tick chờ sau lệnh đó (20 tick = 1 giây).

Đổi phím trong Options > Controls > MultiCmd.

## Build

Cần JDK 21.

- Có Gradle wrapper: `./gradlew build`
- Chưa có wrapper: chạy `gradle wrapper --gradle-version 8.12` một lần (hoặc copy `gradle/`, `gradlew`, `gradlew.bat` từ repo mod khác).
- Push lên GitHub thì workflow `.github/workflows/build.yml` tự build, tải jar ở tab Actions > Artifacts.

Jar nằm ở `build/libs/` (dùng file không có đuôi `-sources`).
