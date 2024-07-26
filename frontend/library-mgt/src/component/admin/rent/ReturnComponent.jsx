import React, { useEffect, useState } from 'react'
import { getLoansWithReturningStatus, getLoansWithReturnStatus, returnLoan } from '../../../service/RentService';
import useNotification from '../../../hooks/useNotification';
import { Button, Table } from 'react-bootstrap';
import Notification from '../../common/Notification';

const ReturnComponent = () => {
    const [rentReturn, setRentReturn] = useState([]);
    const {showError, showSuccess} = useNotification();
    const [tmp, setTmp] = useState(false);

    useEffect(() => {
        fetchLoanReturn();
    },[tmp])

    const fetchLoanReturn = async () => {
        await getLoansWithReturningStatus()
            .then(res => {
                console.log(res);
                if (res?.code === 200) {
                    setRentReturn(res?.data)
                }
            })
            .catch(err => {
                console.error("Lỗi khi lấy tác giả:", err);
                showError('Lỗi khi lấy danh sách cho thuê');
            });

    };

    const handleApprove = async (loanId, bookCopyId) => {
        // call api to change the status into AVAILABLE
        const body = { loanId: loanId, bookCopyId: bookCopyId, note: '' }
        await returnLoan(body).then(res => {
            if (res?.code === 200) {
                setTmp(!tmp);
            }
        }).catch(err => {
            showError("Lỗi khi đồng ý cho mượn sách")
            console.log(err);
        })
    }

    return (
        <div>
            <Notification />
            <div className="d-flex justify-content-between" style={{ marginBottom: '20px' }}>
                <h5>Danh sách đồng ý trả sách</h5>
            </div>
            {rentReturn?.length > 0 ? (
                <Table style={{ fontSize: 'small', boxShadow: "0 0 10px rgba(0, 0, 0, 0.1)" }}>
                    <thead>
                        <tr>
                            <th>Tên người thuê</th>
                            <th>Tên sách</th>
                            <th>Mã cho thuê</th>
                            <th>Ngày cho thuê</th>
                            <th>Ngày trả</th>
                            <th>Ngày quá hạn trả sách</th>
                            <th>Trạng thái</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {rentReturn?.map((r, idx) => (
                            <tr key={idx}>
                                <td className="align-middle">{r?.userName}</td>
                                <td className="align-middle">{r?.bookCopyResponse?.title}</td>
                                <td className="align-middle">{r?.bookCopyResponse?.barcode}</td>
                                <td className="align-middle">{r?.borrowAt}</td>
                                <td className="align-middle">{r?.returnAt}</td>
                                <td className="align-middle">{r?.dueDate}</td>
                                <td className="align-middle">{r?.status}</td>
                                <td className="align-middle">
                                    <Button
                                        as={Button}
                                        onClick={() => handleApprove(r?.loanId, r?.bookCopyResponse?.id)}
                                        style={{
                                            fontSize: 'small',
                                            backgroundColor: '#fff',
                                            border: 'none',
                                            color: '#000',
                                            padding: '0'
                                        }}
                                    >
                                        <i className="bi bi-check-lg"></i>
                                        <span className='m-1'>Chấp nhận</span>
                                    </Button>
                            
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>) : (
                <p>Không có yêu cầu trả sách nào được tìm thấy</p>
            )}


            {/* <CustomModal
                show={showRejectModal}
                handleClose={handleCloseRejectModal}
                title="Xác nhận từ chối mượn sách"
                handleSave={() => handleRejectRent(rentToReject?.loanId, rentToReject?.bookCopyResponse?.id)}
                submitting={submittingReject}
                hasFooter={true}
            >
                <p>Bạn có chắc chắn muốn từ chối danh mục <strong>{rentToReject?.bookCopyResponse?.barcode}</strong> không?</p>
                <textarea value={reason} onChange={(e) => setReason(e.target.value)} cols={55} rows={10}/>
            </CustomModal> */}

        </div>
    )
}

export default ReturnComponent