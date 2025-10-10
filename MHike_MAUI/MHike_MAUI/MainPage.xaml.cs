using System;
using System.Collections.Generic;
using Microsoft.Maui.Storage;
using Microsoft.Maui.ApplicationModel;   // cho Geolocation, MediaPicker
using Microsoft.Maui.Storage;             // cho FileSystem
using System.Threading.Tasks;

namespace MHike_MAUI;

public partial class MainPage : ContentPage
{
    List<string> savedHikes = new();

    public MainPage()
    {
        InitializeComponent();
    }

    protected override async void OnAppearing()
    {
        base.OnAppearing();
        savedHikes = await DataStorage.LoadAsync();
    }

    // ====== SAVE HIKE ======
    private async void OnSaveClicked(object sender, EventArgs e)
    {
        if (string.IsNullOrWhiteSpace(nameEntry.Text) ||
            string.IsNullOrWhiteSpace(locationEntry.Text) ||
            parkingPicker.SelectedItem == null ||
            string.IsNullOrWhiteSpace(lengthEntry.Text) ||
            difficultyPicker.SelectedItem == null)
        {
            await DisplayAlert("Missing Info", "Please fill all required (*) fields.", "OK");
            return;
        }

        // Tự động lấy vị trí GPS
        string gps = "Not Available";
        try
        {
            var location = await Geolocation.GetLastKnownLocationAsync();
            if (location != null)
                gps = $"{location.Latitude:F4}, {location.Longitude:F4}";
        }
        catch { }

        string name = nameEntry.Text;
        string locationTxt = locationEntry.Text;
        string date = datePicker.Date.ToShortDateString();
        string parking = parkingPicker.SelectedItem.ToString();
        string length = lengthEntry.Text;
        string difficulty = difficultyPicker.SelectedItem.ToString();
        string desc = descriptionEditor.Text;
        string weather = weatherEntry.Text;
        string elevation = elevationEntry.Text;

        string summary = $"✅ Hike Saved!\n\n" +
                         $"Name: {name}\n" +
                         $"Location: {locationTxt}\n" +
                         $"Date: {date}\n" +
                         $"Parking: {parking}\n" +
                         $"Length: {length} km\n" +
                         $"Difficulty: {difficulty}\n" +
                         $"Weather: {weather}\n" +
                         $"Elevation: {elevation} m\n" +
                         $"GPS: {gps}\n" +
                         $"Description: {desc}";

        resultLabel.Text = summary;

        // Lưu dữ liệu JSON
        savedHikes.Add($"{name} - {locationTxt} - {date} - GPS: {gps}");
        await DataStorage.SaveAsync(savedHikes);
        await DisplayAlert("Saved", "Your hike has been saved!", "OK");
    }

    // ====== SHOW SAVED ======
    private async void OnShowClicked(object sender, EventArgs e)
    {
        var hikes = await DataStorage.LoadAsync();
        string result = hikes.Count > 0 ? string.Join("\n• ", hikes) : "No hikes saved yet.";
        await DisplayAlert("Saved Hikes", "• " + result, "OK");
    }

    // ====== ADD PHOTO ======
    private async void OnAddPhotoClicked(object sender, EventArgs e)
    {
        try
        {
            var photo = await MediaPicker.PickPhotoAsync();
            if (photo != null)
            {
                var stream = await photo.OpenReadAsync();
                photoPreview.Source = ImageSource.FromStream(() => stream);
            }
        }
        catch (Exception ex)
        {
            await DisplayAlert("Error", ex.Message, "OK");
        }
    }
}
