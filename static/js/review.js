async function loadReviews(hotelId, username, isOnClick, isNext) {
    console.log(isNext);
    let response = await fetch('/jsonReview?hotelId=' + hotelId + '&isOnClick=' + isOnClick + '&isNext=' + isNext, { method: 'get' });

    let json = await response.json();
    let jsonArray = json.reviews;
    console.log(json);

    let thead = document.getElementById('thead');
    thead.innerHTML = "<tr><th>Review ID</th>" +
        "<th>Title</th>" +
        "<th>Text</th>" +
        "<th>Date</th>" +
        "<th>Edit/Delete</th>" +
        "</tr>";

    let tbody = document.getElementById('tbody');
    tbody.innerHTML = '';

    for (let i = 0; i < jsonArray.length; i++) {
        let row = tbody.insertRow(-1);
        let cell1 = row.insertCell(0);
        let cell2 = row.insertCell(1);
        let cell3 = row.insertCell(2);
        let cell4 = row.insertCell(3);
        let cell5 = row.insertCell(4);

        cell1.innerHTML = jsonArray[i].reviewId;
        cell2.innerHTML = jsonArray[i].title;
        cell3.innerHTML = jsonArray[i].reviewText;
        cell4.innerHTML = jsonArray[i].date;

        if(jsonArray[i].user == username){
            cell5.innerHTML = "<a href=/editReview?hotelId=" + hotelId + "&reviewId=" + jsonArray[i].reviewId + ">Edit</a><br>" +
                "<a href=/deleteReview?hotelId=" + hotelId + "&reviewId=" + jsonArray[i].reviewId + ">Delete</a>";
        } else {
            cell5.innerHTML = '';
        }

    }

    let nextButton = "";
    let previousButton = "";


    previousButton = "<button class=\"btn btn-primary mb-2\" onclick=\"loadReviews("+hotelId+",'"+username+"','"+false+"','"+false+"')\">previous page</button>";
    document.getElementById("previousButton").innerHTML = previousButton;

    nextButton = "<button class=\"btn btn-primary mb-2\" onclick=\"loadReviews("+hotelId+",'"+username+"','"+false+"','"+true+"')\">next page</button>";
    document.getElementById("nextButton").innerHTML = nextButton;




}



