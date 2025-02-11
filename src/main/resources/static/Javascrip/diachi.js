
// const host = "https://provinces.open-api.vn/api/";
// var callAPI = (api) => {
//     return axios.get(api)
//         .then((response) => {
//             renderData(response.data, "province");
//         });
// }
// callAPI('https://provinces.open-api.vn/api/?depth=1');
// var callApiDistrict = (api) => {
//     return axios.get(api)
//         .then((response) => {
//             renderData(response.data.districts, "district");
//         });
// }
// var callApiWard = (api) => {
//     return axios.get(api)
//         .then((response) => {
//             renderData(response.data.wards, "ward");
//         });
// }
//
// var renderData = (array, select) => {
//     let row = ' <option disable value="">Chọn</option>';
//     array.forEach(element => {
//         row += `<option value="${element.code}">${element.name}</option>`
//     });
//     document.querySelector("#" + select).innerHTML = row
// }
//
// $("#province").change(() => {
//     $("#ward").html('<option value="">Chọn</option>');
//     callApiDistrict(host + "p/" + $("#province").val() + "?depth=2");
//     updateAddressResult();
// });
// $("#district").change(() => {
//     callApiWard(host + "d/" + $("#district").val() + "?depth=2");
//     updateAddressResult();
//
//
// });
// $("#ward").change(() => {
//     updateAddressResult();
// });
//
// var updateAddressResult = () => {
//     let provinceId = $("#province").val() || "";
//     let districtText = $("#district").val() || "";
//     let wardText = $("#ward").val() || "";
//     let tentinh = $("#province option:selected").text() || "";
//     let tenhuyen = $("#district option:selected").text() || "";
//     let tenxa = $("#ward option:selected").text() || "";
//
//     // let result = `${provinceId} | ${districtText} | ${wardText}`;
//     // $("#result").text(result);
//     let result = `${tentinh} | ${tenhuyen} | ${tenxa}`;
//     $("#result").text(result);
//
//     $("#selectedProvince").val(provinceId);
//     $("#selectedDistrict").val(districtText);
//     $("#selectedWard").val(wardText);
//
//     $("#tentinh").val(tentinh);
//     $("#tenhuyen").val(tenhuyen);
//     $("#tenxa").val(tenxa);
//     let selectedCustomer = {
//         idTinhThanhPho: provinceId,
//         tentinh: tentinh,
//         idQuanHuyen: districtText,
//         tenhuyen: tenhuyen,
//         idXaPhuong: wardText,
//         tenxa: tenxa
//     };
//
//     localStorage.setItem("selectedCustomer", JSON.stringify(selectedCustomer));
// };

// $(document).ready(() => {
//     const savedCustomer = JSON.parse(localStorage.getItem("selectedCustomer"));
//     if (savedCustomer) {
//         const {idTinhThanhPho, tentinh, idQuanHuyen, tenhuyen, idXaPhuong, tenxa} = savedCustomer;
//         console.log("Dữ liệu từ localStorage:", savedCustomer);
//
//         // Gọi lại API để tải danh sách tỉnh thành
//         callAPI('https://provinces.open-api.vn/api/?depth=1').then(() => {
//             $("#province").val(idTinhThanhPho).trigger("change");
//             $("#tentinh").val(tentinh).trigger("change");
//
//
//             callApiDistrict(`https://provinces.open-api.vn/api/p/${idTinhThanhPho}?depth=2`).then(() => {
//                 $("#district").val(idQuanHuyen).trigger("change");
//                 $("#tenhuyen").val(tenhuyen).trigger("change");
//                 callApiWard(`https://provinces.open-api.vn/api/d/${idQuanHuyen}?depth=2`).then(() => {
//                     $("#ward").val(idXaPhuong).trigger("change");
//                     $("#tenxa").val(tenxa).trigger("change");
//                 });
//             });
//         });
//
//         $("#selectedProvince").val(idTinhThanhPho);
//         $("#selectedDistrict").val(idQuanHuyen);
//         $("#selectedWard").val(idXaPhuong);
//
//         $("#tentinh").val(tenxa);  // Tên tỉnh
//         $("#tenhuyen").val(tenhuyen);    // Tên quận
//         $("#tenxa").val(tenxa);        // Tên phường
//
//         // Hiển thị tên địa chỉ trong kết quả
//         let result = `${tentinh} | ${tenhuyen} | ${tenxa}`;
//         $("#result").text(result)
//     }
// });


const API_KEY = "fzAtlNx7be0lF76zrdcwDSMpuyokgXrt2BDTDprF";
const AUTOCOMPLETE_URL = "https://rsapi.goong.io/Place/AutoComplete";
const GEOCODE_URL = "https://rsapi.goong.io/Geocode";

const searchBox = document.getElementById('searchBox');
const suggestionsList = document.getElementById('suggestions');
let selectedAddress = ""; // Biến để lưu địa chỉ đã chọn

// Lấy gợi ý địa chỉ
searchBox.addEventListener('input', function() {
    const query = this.value;

    if (query.trim() === "") {
        suggestionsList.innerHTML = "";
        return;
    }

    fetch(`${AUTOCOMPLETE_URL}?api_key=${API_KEY}&input=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(data => {
            const suggestions = data.predictions || [];
            suggestionsList.innerHTML = "";

            suggestions.forEach(item => {
                const listItem = document.createElement('li');
                listItem.textContent = item.description;

                listItem.addEventListener('click', () => {
                    handleAddressSelection(item.description);
                });

                suggestionsList.appendChild(listItem);
            });
        })
        .catch(error => {
            console.error("Lỗi khi gọi API:", error);
        });
});

// Xử lý chọn địa chỉ
function handleAddressSelection(description) {
    searchBox.value = description;
    suggestionsList.innerHTML = "";
    selectedAddress = description; // Lưu địa chỉ đã chọn
}


