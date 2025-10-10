using System.Text.Json;

namespace MHike_MAUI
{
    public static class DataStorage
    {
        // Tạo đường dẫn lưu file hikes.json trong bộ nhớ app
        static string filePath = Path.Combine(FileSystem.AppDataDirectory, "hikes.json");

        // Lưu danh sách các hike vào file JSON
        public static async Task SaveAsync(List<string> hikes)
        {
            string json = JsonSerializer.Serialize(hikes);
            await File.WriteAllTextAsync(filePath, json);
        }

        // Đọc lại danh sách từ file JSON (nếu có)
        public static async Task<List<string>> LoadAsync()
        {
            if (File.Exists(filePath))
            {
                string json = await File.ReadAllTextAsync(filePath);
                return JsonSerializer.Deserialize<List<string>>(json) ?? new();
            }
            return new();
        }
    }
}
