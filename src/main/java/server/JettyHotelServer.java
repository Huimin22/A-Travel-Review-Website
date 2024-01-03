package server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class JettyHotelServer {
	public static final int PORT = 8080;
//	private Object data;

//	public JettyHotelServer(Object data) {
//		this.data = data;
//	}

	public void start() {
//		DatabaseHandler dbhandler = DatabaseHandler.getInstance();
//		dbhandler.createUsersTable();
//		dbhandler.createHotelsTable();
//		dbhandler.createReviewsTable();

		Server server = new Server(PORT);
		ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		handler.setAttribute("data", data);

		handler.addServlet(LoginServlet.class, "/login");
		handler.addServlet(LogoutServlet.class, "/logout");
		handler.addServlet(RegisterServlet.class, "/register");
		handler.addServlet(HomeServlet.class, "/home");
		handler.addServlet(SearchHotelServlet.class, "/searchHotel");
		handler.addServlet(HotelInfoServlet.class, "/hotelInfo");
		handler.addServlet(AddReviewServlet.class, "/addReview");
		handler.addServlet(EditReviewServlet.class, "/editReview");
		handler.addServlet(DeleteReviewServlet.class, "/deleteReview");
		handler.addServlet(WeatherServlet.class, "/getWeather");
		handler.addServlet(ReviewServlet.class, "/jsonReview");

		handler.addServlet(AddFavoritesServlet.class, "/insertFavorites");
		handler.addServlet(ShowFavoritesServlet.class, "/showFavorites");
		handler.addServlet(ClearFavoriteServlet.class, "/clearFavoriteList");
		handler.addServlet(AddLinksServlet.class, "/insertLinks");
		handler.addServlet(ShowHistoryServlet.class, "/showHistory");
		handler.addServlet(ClearHistoryServlet.class, "/clearHistory");

		VelocityEngine velocity = new VelocityEngine();
		velocity.init();
		handler.setAttribute("templateEngine", velocity);

		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setResourceBase("static");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resourceHandler, handler});
		server.setHandler(handlers);

		try {
			server.start();
			server.join();
		} catch (Exception e) {
			System.out.println("Exception occurred while running the server: " + e);
		}

	}

	public static void main(String[] args)  {
		// FILL IN CODE, and add more classes as needed
//		ThreadSafeHotelData hotelData = new ThreadSafeHotelData();
//		SearchHelper searchHelper = new SearchHelper();
//		searchHelper.processArgs(args);
//		String hotelsFilePath = searchHelper.getArgValue("-hotels");
//		String reviewsFilePath = searchHelper.getArgValue("-reviews");
//		int numOfThead = Integer.parseInt(searchHelper.getArgValue("-threads"));
//		MultithreadedDirectoryTraverser traverser = new MultithreadedDirectoryTraverser(numOfThead);
//
//		ThreadSafeHotelData data = traverser.loadData(hotelsFilePath, reviewsFilePath);
//		data.buildWordMap();

//		JettyHotelServer jettyHotelServer= new JettyHotelServer(hotelData);
		JettyHotelServer jettyHotelServer= new JettyHotelServer();
		jettyHotelServer.start();

	}
}