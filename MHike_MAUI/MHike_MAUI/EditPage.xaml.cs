using Microsoft.Maui.Controls;
using Microsoft.Maui.Media;
using System;
using System.IO;
using System.Threading.Tasks;

namespace MHike_MAUI
{
    public partial class EditPage : ContentPage
    {
        private Hike currentHike;
        private string selectedPhotoPath = string.Empty;

        public EditPage(Hike hike)
        {
            InitializeComponent(); // ⚠️ phải luôn gọi đầu tiên

            // 🔹 Kiểm tra dữ liệu nhận vào
            if (hike == null)
            {
                Console.WriteLine("⚠️ EditPage: Received null hike!");
                DisplayAlert("Error", "No hike data found.", "OK");
                currentHike = new Hike();
            }
            else
            {
                currentHike = hike;
            }

            try
            {
                LoadHikeData(); // 🔹 gọi sau khi UI đã sẵn sàng
            }
            catch (Exception ex)
            {
                Console.WriteLine($"❌ LoadHikeData() failed: {ex.Message}");
                DisplayAlert("Error", "Failed to load hike data.", "OK");
            }
        }

        // 🧭 Load dữ liệu cũ vào form
        private void LoadHikeData()
        {
            if (currentHike == null)
                return;

            nameEntry.Text = currentHike.Name ?? "";
            locationEntry.Text = currentHike.Location ?? "";
            datePicker.Date = currentHike.Date == default ? DateTime.Today : currentHike.Date;
            lengthEntry.Text = currentHike.Length > 0 ? currentHike.Length.ToString() : "";
            elevationEntry.Text = currentHike.Elevation > 0 ? currentHike.Elevation.ToString() : "";
            difficultyEntry.Text = currentHike.Difficulty ?? "";
            descriptionEditor.Text = currentHike.Description ?? "";
            weatherEntry.Text = currentHike.Weather ?? "";
            parkingEntry.Text = currentHike.Parking ?? "";

            if (!string.IsNullOrEmpty(currentHike.PhotoPath) && File.Exists(currentHike.PhotoPath))
            {
                photoPreview.Source = ImageSource.FromFile(currentHike.PhotoPath);
                photoPreview.IsVisible = true;
                selectedPhotoPath = currentHike.PhotoPath;
            }

            Console.WriteLine($"✅ Loaded hike: {currentHike.Name}");
        }

        // 📸 Thay đổi ảnh mới
        private async void OnChangePhotoClicked(object sender, EventArgs e)
        {
            try
            {
                var photo = await MediaPicker.PickPhotoAsync();
                if (photo != null)
                {
                    selectedPhotoPath = photo.FullPath;
                    using var stream = await photo.OpenReadAsync();
                    photoPreview.Source = ImageSource.FromStream(() => stream);
                    photoPreview.IsVisible = true;
                }
            }
            catch (FeatureNotSupportedException)
            {
                await DisplayAlert("Error", "This device does not support photo picking.", "OK");
            }
            catch (PermissionException)
            {
                await DisplayAlert("Permission Denied", "Please allow photo access in settings.", "OK");
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", $"Failed to pick photo: {ex.Message}", "OK");
            }
        }

        // 💾 Lưu thay đổi
        private async void OnSaveChangesClicked(object sender, EventArgs e)
        {
            if (string.IsNullOrWhiteSpace(nameEntry.Text) || string.IsNullOrWhiteSpace(locationEntry.Text))
            {
                await DisplayAlert("Missing Info", "Please enter both Name and Location.", "OK");
                return;
            }

            double.TryParse(lengthEntry.Text, out double length);
            double.TryParse(elevationEntry.Text, out double elevation);

            currentHike.Name = nameEntry.Text.Trim();
            currentHike.Location = locationEntry.Text.Trim();
            currentHike.Date = datePicker.Date;
            currentHike.Length = length;
            currentHike.Elevation = elevation;
            currentHike.Difficulty = difficultyEntry.Text?.Trim() ?? "";
            currentHike.Description = descriptionEditor.Text?.Trim() ?? "";
            currentHike.Weather = weatherEntry.Text?.Trim() ?? "";
            currentHike.Parking = parkingEntry.Text?.Trim() ?? "";
            currentHike.PhotoPath = selectedPhotoPath;

            await DataStorage.UpdateHikeAsync(currentHike);
            await DisplayAlert("✅ Updated", "Hike details updated successfully!", "OK");

            await Navigation.PopAsync(); // 🔙 Quay lại danh sách
        }
    }
}
    