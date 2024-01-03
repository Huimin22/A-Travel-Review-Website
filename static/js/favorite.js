async function addFavorite(hotelId, hotelName){
    let response = await fetch('/insertFavorites?hotelId='+hotelId+"&hotelName="+hotelName, {method:'get'});
    let text = await response.text();
       let original = "";
       let duplicate = "";
    if(text.trim() == 'false'){
        original = "<div class=\"alert alert-success\" role=\"alert\">Added to favorites</div>"
    }else{
         duplicate = "<div class=\"alert alert-danger\"role=\"alert\">Already added as favorite</div>"
    }

    let originalElement = document.getElementById("original");
    if (originalElement) {
        originalElement.innerHTML = original;
        // setTimeout(function() { originalElement.innerHTML = ''; }, 80);
    } else {
        console.log('Element with id "original" not found');
    }

    let duplicateElement = document.getElementById("duplicate");
    if (duplicateElement) {
        duplicateElement.innerHTML = duplicate;
        // setTimeout(function() { duplicateElement.innerHTML = ''; }, 80);
    } else {
        console.log('Element with id "duplicate" not found');
    }

    console.log(text);
}


// async function addFavorite(hotelId, hotelName){
//     let response = await fetch('/insertFavorites?hotelId='+hotelId+"&hotelName="+hotelName, {method:'get'});
//     let text = await response.text();
//     let success = "";
//     let existed = "";
//
//     if(text.trim() == 'false'){
//         success = "<div class=\"alert alert-success\" role=\"alert\">Added to favorites</div>";
//     } else {
//         existed = "<div class=\"alert alert-danger\" role=\"alert\">Already added as favorite</div>";
//     }
//
//     let successElement = document.getElementById("success");
//     if (successElement) {
//         successElement.innerHTML = success;
//     } else {
//         console.log('Element with id "success" not found');
//     }
//
//     let existedElement = document.getElementById("existed");
//     if (existedElement) {
//         existedElement.innerHTML = existed;
//     } else {
//         console.log('Element with id "existed" not found');
//     }
//
//     console.log(text);
// }

