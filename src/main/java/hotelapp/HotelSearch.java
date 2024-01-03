package hotelapp;

public class HotelSearch {
    public static void main(String[] args) {
        SearchHelper searchHelper = new SearchHelper();
        searchHelper.processArgs(args);
        String hotelsFilePath = searchHelper.getArgValue("-hotels");
        String reviewsFilePath = searchHelper.getArgValue("-reviews");
        String outputFilePath = searchHelper.getArgValue("-output");
        int numOfThead = Integer.parseInt(searchHelper.getArgValue("-threads"));

        MultithreadedDirectoryTraverser traverser = new MultithreadedDirectoryTraverser(numOfThead);
        ThreadSafeHotelData hotelReviewData = traverser.loadData(hotelsFilePath, reviewsFilePath);

        if (outputFilePath != null) {
            searchHelper.writeFile(hotelReviewData, outputFilePath, reviewsFilePath);
        }
        else {
            searchHelper.handleUserInput(hotelReviewData);
        }
    }

}


