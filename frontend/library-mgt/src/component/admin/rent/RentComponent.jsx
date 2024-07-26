import React, { useEffect, useState } from 'react'
import useNotification from '../../../hooks/useNotification';
import { approveLoan, getLoansWithPendingStatus, rejectLoan } from '../../../service/RentService';
import Notification from '../../common/Notification';
import { Button, Table } from 'react-bootstrap';
import CustomModal from '../../common/CustomModal';

const RentComponent = () => {
    const [rent, setRent] = useState([]);
    const { showError, showSuccess } = useNotification();
    const [rentToReject, setRentToReject] = useState();
    const [showRejectModal, setShowRejectModal] = useState(false);
    const [submittingReject, setSubmittingReject] = useState(false);
    const [tmp, setTmp] = useState(false);
    const [reason, setReason] = useState('');

    useEffect(() => {
        fetchLoans();
    }, [tmp]);

    const fetchLoans = async () => {
        await getLoansWithPendingStatus()
            .then(res => {
                if (res?.code === 200) {
                    setRent(res?.data)
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
        await approveLoan(body).then(res => {
            if (res?.code === 200) {
                handleCloseRejectModal()
                setTmp(!tmp);
            }
        }).catch(err => {
            showError("Lỗi khi đồng ý cho mượn sách")
            console.log(err);
        })
    }


    const handleShowRejectModal = (loan) => {
        setRentToReject(loan);
        setShowRejectModal(true);
    };

    const handleCloseRejectModal = () => {
        setReason('')
        setRentToReject(null);
        setShowRejectModal(false);
    };

    const handleRejectRent = async (loanId, bookCopyId) => {
        // console.log(loanId, bookCopyId, reason);
        // call api to change the status into AVAILABLE
        const body = { loanId: loanId, bookCopyId: bookCopyId, note: reason }
        await rejectLoan(body).then(res => {
            if (res?.code === 200) {
                handleCloseRejectModal()
                setReason('')
                setTmp(!tmp);
            }
        }).catch(err => {
            showError("Lỗi khi từ chối cho mượn sách")
            console.log(err);
        })
    }


    return (
        <div>
            <Notification />
            <div className="d-flex justify-content-between" style={{ marginBottom: '20px' }}>
                <h5>Danh sách đồng ý cho thuê sách</h5>
            </div>
            {rent?.length > 0 ? (
                <Table style={{ fontSize: 'small', boxShadow: "0 0 10px rgba(0, 0, 0, 0.1)" }}>
                    <thead>
                        <tr>
                            <th>Tên người thuê</th>
                            <th>Tên sách</th>
                            <th>Mã cho thuê</th>
                            <th>Ngày cho thuê</th>
                            <th>Ngày trả</th>
                            <th>Ngày quá hạn trả sách</th>
                            <th>Ghi chú</th>
                            <th>Trạng thái</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        {rent?.map((r, idx) => (
                            <tr key={idx}>
                                <td className="align-middle">{r?.userName}</td>
                                <td className="align-middle">{r?.bookCopyResponse?.title}</td>
                                <td className="align-middle">{r?.bookCopyResponse?.barcode}</td>
                                <td className="align-middle">{r?.borrowAt}</td>
                                <td className="align-middle">{r?.returnAt}</td>
                                <td className="align-middle">{r?.dueDate}</td>
                                <td className="align-middle">{r?.note}</td>
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
                                    <Button
                                        as={Button}
                                        style={{
                                            fontSize: 'small',
                                            backgroundColor: '#fff',
                                            border: 'none',
                                            color: '#000',
                                            padding: '0',
                                            marginLeft: '5px'
                                        }}
                                        onClick={() => handleShowRejectModal(r)}
                                    >
                                        <i className="bi bi-x-square"></i>
                                        <span className='m-1'>Từ chối</span>
                                    </Button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </Table>) : (
                <p>Không có yêu cầu mượn sách nào được tìm thấy</p>
            )}


            <CustomModal
                show={showRejectModal}
                handleClose={handleCloseRejectModal}
                title="Xác nhận từ chối mượn sách"
                handleSave={() => handleRejectRent(rentToReject?.loanId, rentToReject?.bookCopyResponse?.id)}
                submitting={submittingReject}
                hasFooter={true}
            >
                <p>Bạn có chắc chắn muốn từ chối danh mục <strong>{rentToReject?.bookCopyResponse?.barcode}</strong> không?</p>
                <textarea value={reason} onChange={(e) => setReason(e.target.value)} cols={55} rows={10}/>
            </CustomModal>

        </div>
    )
}

export default RentComponent