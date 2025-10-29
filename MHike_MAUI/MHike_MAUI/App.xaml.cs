

namespace MHike_MAUI
{
    public partial class App : Application
    {
        public App()
        {
            InitializeComponent();

            // 🔹 Load dữ liệu khi app khởi động
            _ = DataStorage.LoadAsync();

            // 🔹 Bọc MainPage trong NavigationPage để bật Navigation.PushAsync hoạt động
            MainPage = new NavigationPage(new MainPage())
            {
                BarBackgroundColor = Color.FromArgb("#007AFF"),
                BarTextColor = Colors.White
            };
        }
    }
}
