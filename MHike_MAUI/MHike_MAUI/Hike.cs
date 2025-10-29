namespace MHike_MAUI
{
    public class Hike
    {
        public string Name { get; set; }
        public string Location { get; set; }
        public DateTime Date { get; set; }
        public string Parking { get; set; }
        public double Length { get; set; }
        public string Difficulty { get; set; }
        public string Description { get; set; }
        public string Weather { get; set; }
        public double Elevation { get; set; }
        public string PhotoPath { get; set; } // 🖼️ lưu đường dẫn ảnh
        public double Latitude { get; set; }
        public double Longitude { get; set; }
    }
}
