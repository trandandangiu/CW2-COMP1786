using System;
using Microsoft.Maui.Controls;
using Microsoft.Maui.ApplicationModel;
using Microsoft.Maui.Media;
using System.Threading.Tasks;
using System.IO;

namespace MHike_MAUI
{
    public partial class MainPage : ContentPage
    {
        private string selectedPhotoPath = null;

        public MainPage()
        {
            InitializeComponent();
            _ = DataStorage.LoadAsync(); // Load dữ liệu khi mở app
        }

        // 📸 Chọn ảnh và hiển thị preview
        private async void OnAddPhotoClicked(object sender, EventArgs e)
        {
            try
            {
                var photo = await MediaPicker.PickPhotoAsync();
                if (photo != null)
                {
                    selectedPhotoPath = photo.FullPath;
                    var stream = await photo.OpenReadAsync();
                    photoPreview.Source = ImageSource.FromStream(() => stream);
                    photoPreview.IsVisible = true;
                    await DisplayAlert("Photo Added", $"Selected: {photo.FileName}", "OK");
                }
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", $"Failed to pick photo: {ex.Message}", "OK");
            }
        }

        // 💾 Lưu dữ liệu từ form
        private async void OnSaveClicked(object sender, EventArgs e)
        {
            try
            {
                if (string.IsNullOrWhiteSpace(nameEntry.Text) || string.IsNullOrWhiteSpace(locationEntry.Text))
                {
                    await DisplayAlert("Missing Info", "Please enter Hike Name and Location.", "OK");
                    return;
                }

                double.TryParse(lengthEntry.Text, out double lengthVal);
                double.TryParse(elevationEntry.Text, out double elevationVal);

                var hike = new Hike
                {
                    Name = nameEntry.Text.Trim(),
                    Location = locationEntry.Text.Trim(),
                    Date = datePicker.Date,
                    Parking = parkingPicker.SelectedItem?.ToString() ?? "Unknown",
                    Length = lengthVal,
                    Difficulty = difficultyPicker.SelectedItem?.ToString() ?? "Unknown",
                    Description = descriptionEditor.Text?.Trim() ?? "",
                    Weather = weatherEntry.Text?.Trim() ?? "",
                    Elevation = elevationVal,
                    PhotoPath = selectedPhotoPath,
                    Latitude = 0,
                    Longitude = 0
                };

                await DataStorage.AddOrUpdateHikeAsync(hike);
                await DisplayAlert("✅ Success", "Your hike has been saved successfully!", "OK");

                // Reset form
                nameEntry.Text = locationEntry.Text = descriptionEditor.Text = weatherEntry.Text =
                    elevationEntry.Text = lengthEntry.Text = "";
                parkingPicker.SelectedIndex = -1;
                difficultyPicker.SelectedIndex = -1;
                photoPreview.IsVisible = false;
                selectedPhotoPath = null;
            }
            catch (Exception ex)
            {
                await DisplayAlert("Error", $"Failed to save hike: {ex.Message}", "OK");
            }
        }

        // 🗂️ Hiển thị danh sách hikes đã lưu
        private async void OnShowClicked(object sender, EventArgs e)
        {
            var hikes = DataStorage.GetAllHikes();
            if (hikes == null || hikes.Count == 0)
            {
                await DisplayAlert("No Data", "No hikes saved yet.", "OK");
                return;
            }

            var stack = new VerticalStackLayout
            {
                Padding = new Thickness(15),
                Spacing = 12
            };

            foreach (var hike in hikes)
            {
                var hikeImage = new Image
                {
                    Source = string.IsNullOrEmpty(hike.PhotoPath)
                        ? "hike_placeholder.png"
                        : ImageSource.FromFile(hike.PhotoPath),
                    WidthRequest = 80,
                    HeightRequest = 80,
                    Aspect = Aspect.AspectFill,
                    Margin = new Thickness(0, 0, 10, 0),
                    VerticalOptions = LayoutOptions.Start
                };

                var details = new VerticalStackLayout
                {
                    Spacing = 3,
                    Children =
                    {
                        new Label
                        {
                            Text = $"🏞️ {hike.Name}",
                            FontAttributes = FontAttributes.Bold,
                            FontSize = 18,
                            TextColor = Colors.Black
                        },
                        new Label
                        {
                            Text = $"📍 {hike.Location}",
                            FontSize = 14,
                            TextColor = Colors.Gray
                        },
                        new Label
                        {
                            Text = $"📅 {hike.Date:dd/MM/yyyy}  |  ⛰️ {hike.Length} km",
                            FontSize = 13,
                            TextColor = Colors.DarkGray
                        },
                        new Label
                        {
                            Text = $"🧭 GPS: {hike.Latitude}, {hike.Longitude}",
                            FontSize = 12,
                            TextColor = Colors.Gray
                        }
                    }
                };

                // 🔹 Hàng nút "Edit" và "Delete"
                var buttonRow = new HorizontalStackLayout
                {
                    Spacing = 15,
                    Children =
                    {
                        new Button
                        {
                            Text = "✏️ Edit",
                            BackgroundColor = Color.FromArgb("#007AFF"),
                            TextColor = Colors.White,
                            CornerRadius = 12,
                            Padding = new Thickness(12, 4),
                            Command = new Command(async () =>
                            {
                                await Navigation.PushAsync(new EditPage(hike));
                            })
                        },
                        new Button
                        {
                            Text = "🗑️ Delete",
                            BackgroundColor = Color.FromArgb("#FF3B30"),
                            TextColor = Colors.White,
                            CornerRadius = 12,
                            Padding = new Thickness(12, 4),
                            Command = new Command(async () =>
                            {
                                bool confirm = await DisplayAlert("Confirm", $"Delete {hike.Name}?", "Yes", "No");
                                if (confirm)
                                {
                                    await DataStorage.DeleteHikeAsync(hike.Name);
                                    await DisplayAlert("Deleted", $"{hike.Name} has been removed.", "OK");
                                    await Navigation.PopAsync(); // Quay lại main để reload
                                }
                            })
                        }
                    }
                };

                var card = new Frame
                {
                    BackgroundColor = Colors.White,
                    CornerRadius = 16,
                    HasShadow = true,
                    Padding = 12,
                    Content = new VerticalStackLayout
                    {
                        Spacing = 5,
                        Children =
                        {
                            new HorizontalStackLayout
                            {
                                Children = { hikeImage, details }
                            },
                            buttonRow
                        }
                    }
                };

                stack.Children.Add(card);
            }

            var listPage = new ContentPage
            {
                Title = "📍 Saved Hikes",
                BackgroundColor = Color.FromArgb("#F8F9FB"),
                Content = new ScrollView { Content = stack }
            };

            await Navigation.PushAsync(listPage);
        }
    }
}
