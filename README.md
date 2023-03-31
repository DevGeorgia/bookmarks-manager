# bookmarks-manager

Manage your bookmarks with Spring Batch

If you're like me, you probably have a lot of bookmarks or bookmark folders : "I'll keep it here to read it later when I'll need it". And you keep a lot !

With this project you can export them quickly and transform them to a CSV file, so you can manage your bookmarks whatever you want (excel, drive, notion...)

I've imported mine in Notion just for the example :

![import-bookmarks-notion](/screen/import_bookmark_notion.png)

## Implemented Services

For now I only have on service implemented but I plan to add more.

### Export from Chrome Bookmarks

You can export your bookmarks from your Chrome browser and turn them into a CSV file by calling a simple REST service.

* Request type : GET 
* Request URI : /api/job/start/ChromeExportJob 
* No headers, no params

The input and output path are chosen by default : 

* Input : %homepath%\AppData\Local\Google\Chrome\User Data\Default\Bookmarks 
* Output : %homepath%\AppData\Local\Temp\BookmarksManager\export_chrome_bookmarks.csv

The service will not work for MAC users because the bookmarks are stored in a different location

![import-bookmarks-service](/screen/restchromejob.png)
