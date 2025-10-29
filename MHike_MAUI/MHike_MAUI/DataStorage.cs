using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.Maui.Storage;

namespace MHike_MAUI
{
    public static class DataStorage
    {
        // 📂 Đường dẫn file JSON để lưu dữ liệu trong AppDataDirectory
        private static readonly string filePath =
            Path.Combine(FileSystem.AppDataDirectory, "hikes.json");

        // 🧠 Cache tạm trong bộ nhớ
        private static List<Hike> hikes = new();

        // ⚙️ Cài đặt serialize để format đẹp + không lỗi tiếng Việt
        private static readonly JsonSerializerOptions jsonOptions = new()
        {
            WriteIndented = true,
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            Encoder = System.Text.Encodings.Web.JavaScriptEncoder.UnsafeRelaxedJsonEscaping
        };

        // 📥 Tải dữ liệu từ file (gọi khi khởi động)
        public static async Task<List<Hike>> LoadAsync()
        {
            try
            {
                if (File.Exists(filePath))
                {
                    string json = await File.ReadAllTextAsync(filePath);
                    var list = JsonSerializer.Deserialize<List<Hike>>(json, jsonOptions);
                    if (list != null)
                        hikes = list;
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DataStorage] LoadAsync error: {ex.Message}");
            }

            return hikes;
        }

        // 💾 Lưu danh sách hikes xuống file
        public static async Task SaveAsync()
        {
            try
            {
                string json = JsonSerializer.Serialize(hikes, jsonOptions);
                await File.WriteAllTextAsync(filePath, json);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"[DataStorage] SaveAsync error: {ex.Message}");
            }
        }

        // ➕ Thêm hoặc cập nhật 1 hike (nếu đã tồn tại cùng tên và ngày)
        public static async Task AddOrUpdateHikeAsync(Hike hike)
        {
            if (hike == null) return;

            var existing = hikes.FirstOrDefault(h =>
                h.Name.Equals(hike.Name, StringComparison.OrdinalIgnoreCase)
                && h.Date.Date == hike.Date.Date);

            if (existing != null)
            {
                var index = hikes.IndexOf(existing);
                hikes[index] = hike;
            }
            else
            {
                hikes.Add(hike);
            }

            await SaveAsync();
        }

        // 🟦 Cập nhật một hike đã tồn tại
        public static async Task UpdateHikeAsync(Hike updated)
        {
            if (updated == null) return;

            var existing = hikes.FirstOrDefault(h =>
                h.Name.Equals(updated.Name, StringComparison.OrdinalIgnoreCase)
                && h.Date.Date == updated.Date.Date);

            if (existing != null)
            {
                var index = hikes.IndexOf(existing);
                hikes[index] = updated;
                await SaveAsync();
            }
        }

        // 🗑️ Xóa một hike theo tên
        public static async Task DeleteHikeAsync(string name)
        {
            hikes.RemoveAll(h => h.Name.Equals(name, StringComparison.OrdinalIgnoreCase));
            await SaveAsync();
        }

        // 📋 Lấy toàn bộ danh sách hikes
        public static List<Hike> GetAllHikes()
        {
            return hikes ?? new();
        }

        // 🧹 Xóa toàn bộ dữ liệu
        public static async Task ClearAsync()
        {
            hikes.Clear();
            if (File.Exists(filePath))
                File.Delete(filePath);
            await Task.CompletedTask;
        }

        // 🧭 Load nhanh (nếu app đã load rồi thì không đọc lại từ file)
        public static List<Hike> GetCachedHikes()
        {
            return hikes;
        }
    }
}
