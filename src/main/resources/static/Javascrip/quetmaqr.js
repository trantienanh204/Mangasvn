// tạo mã
    function generateQRCode(buttonElement) {
        // Lấy giá trị mã sản phẩm chi tiết từ thuộc tính data-id
        const maspct = buttonElement.getAttribute('data-id');

        const qrCodeContainer = document.getElementById('qrCodeContainer');
        qrCodeContainer.style.display = "block";
        const maspctText = document.getElementById('maspct');
        maspctText.textContent = `Mã sản phẩm: ${maspct}`;
        QRCode.toDataURL(maspct, function (err, url) {
            if (err) {
                console.error("Error generating QR Code:", err);
                return;
            }
            // Gán URL ảnh mã QR vào thẻ img
            const qrCodeImage = document.getElementById('qrCodeImage');
            qrCodeImage.src = url;
        });
    }


// quét mã
const startScanButton = document.getElementById('startScanButton');
const qrScanContainer = document.getElementById('qrScanContainer');
const video = document.getElementById('qrVideo');
const canvas = document.getElementById('canvas');
const ctx = canvas.getContext('2d');
let scanInterval;
let isScanning = false; // Trạng thái quét

startScanButton.addEventListener('click', async () => {
    if (!isScanning) {
        // Bật camera và bắt đầu quét
        try {
            const stream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: "environment" } });
            video.srcObject = stream;

            // Hiển thị vùng quét và đổi trạng thái nút
            qrScanContainer.style.display = 'block';
            startScanButton.textContent = "Tắt quét";
            isScanning = true;

            // Bắt đầu quét mã QR định kỳ
            scanInterval = setInterval(scanQRCode, 500);
        } catch (err) {
            console.error("Không thể bật camera:", err);
            alert("Không thể truy cập camera. Vui lòng kiểm tra cài đặt.");
        }
    } else {
        // Tắt camera và dừng quét
        stopScanning();
    }
});

function scanQRCode() {
    const videoElement = document.getElementById("qrVideo");
    const canvasElement = document.createElement("canvas");
    const canvasContext = canvasElement.getContext("2d");

    canvasElement.width = videoElement.videoWidth;
    canvasElement.height = videoElement.videoHeight;

    canvasContext.drawImage(videoElement, 0, 0, canvasElement.width, canvasElement.height);

    const imageData = canvasContext.getImageData(0, 0, canvasElement.width, canvasElement.height);
    const qrCodeResult = jsQR(imageData.data, canvasElement.width, canvasElement.height);
    let hasScanned = false;

    if (qrCodeResult && !hasScanned) {
        const maspct = qrCodeResult.data;
        console.error('mã sp:', maspct);
        hasScanned = true;
        fetch('http://localhost:8080/panda/banhangoffline/taohdctqr', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                maspct: maspct  // Dữ liệu bạn gửi qua request
            })
        })
        .then(response => response.json()
        .then(data => ({ ok: response.ok, message: data.message })))
        .then(({ ok, message }) => {
            stopScanning();

            Swal.fire({
                title: 'Thông báo',
                text: message,
                icon: ok ? 'success' : 'error',
                confirmButtonText: 'OK'
            }).then(() => {
                if (ok) {
                    location.reload(); // Reload trang nếu thành công
                }
            });
        })
        .catch(error => {
            console.error('Lỗi:', error);
            Swal.fire({
                title: 'Thông báo',
                text: 'Đã xảy ra lỗi ',
                icon: 'error',
                confirmButtonText: 'OK'
            });
        });
    }else if (hasScanned) {
        console.log('Đã quét mã QR. Không quét thêm nữa.');
        // location.reload();
    }
}

function stopScanning() {
    // Dừng quét
    clearInterval(scanInterval);

    // Tắt camera
    const stream = video.srcObject;
    if (stream) {
        const tracks = stream.getTracks();
        tracks.forEach(track => track.stop());
    }

    // Đặt lại trạng thái
    isScanning = false;
    startScanButton.textContent = "QR Code";
    qrScanContainer.style.display = 'none';
}

