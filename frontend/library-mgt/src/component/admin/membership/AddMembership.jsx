import React, { useEffect, useState } from 'react'
import { useAuth } from '../../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import useNotification from '../../../hooks/useNotification';
import { Button, Form, Spinner } from 'react-bootstrap';
import Notification from '../../common/Notification';
import TextInput from '../../common/TextInput';
import { addMemberSub } from '../../../service/MemberShipService';
import { getBenefits } from '../../../service/BenefitService';

const AddMembership = () => {
    const navigate = useNavigate();
    const { user } = useAuth();
    const { showError, showSuccess } = useNotification();
    const [submitting, setSubmitting] = useState(false);
    const [benefits, setBenefits] = useState([]);

    useEffect(() => {
        fetchBenefits();
    }, [])

    const fetchBenefits = async () => {
        await getBenefits().then(resp => {
            if (resp?.status === 200) {
                setBenefits(resp?.data)
            }
        })
    }

    const [formData, setFormData] = useState({
        email: user?.email,
        nameSubscription: '',
        fee_member: 0,
        expireDate: 0,
        maxBook: 0,
        selectedBenefits: []
    });

    const handleChange = (e) => {
        const { name, value, type } = e.target;
        const newValue = type === 'number' ? parseFloat(value) : value;
        setFormData((prevData) => ({
            ...prevData,
            [name]: newValue
        }));
    };

    const handleBenefitChange = (e) => {
        const { value, checked } = e.target;

        setFormData((prevState) => {
            const selectedBenefits = checked
                ? [...prevState.selectedBenefits, value]
                : prevState.selectedBenefits.filter((benefit) => benefit !== value);

            return {
                ...prevState,
                selectedBenefits
            };
        });
    };


    const validateForm = () => {
        const { nameSubscription, fee_member, maxBook } = formData;
        const nameSubscriptionPattern = /^[a-zA-Z0-9\s]+$/;
        if (!nameSubscription || !nameSubscriptionPattern.test(nameSubscription)) {
            showError('Tên gói sai định dạng');
            return false;
        }
        if (nameSubscription != undefined && nameSubscription.trim() === '') {
            showError('Vui kiểm tra tên gói');
            return false;
        }
        if (parseFloat(fee_member) < 0 || parseFloat(maxBook) < 0) {
            showError('Giá gói không thể nhỏ hơn hoặc bằng 0');
            return false;
        }
        return true;
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) {
            // showError('Vui kiểm tra tên gói hoặc giá không thể nhỏ hơn 0');
            return;
        }
        console.log(formData);
        setSubmitting(true);
        await new Promise(r => setTimeout(r, 2000));

        try {
            await addMemberSub(formData).then(resp => {
                if (resp?.code === 200) {
                    showSuccess(resp?.data?.message);
                    setFormData({
                        email: user?.email,
                        nameSubscription: '',
                        fee_member: 0,
                        expireDate: 0,
                        maxBook: 0,
                        selectedBenefits: []
                    });

                    navigate('/admin/membership', { state: { success: resp?.data?.message } });
                }
            }).catch(err => showError("Gói thành viên đã tồn tại"));
            // await timer;


        } catch (error) {
            console.error("Lỗi thêm gói: ", error);
            setFormData({
                email: user?.email,
                nameSubscription: '',
                fee_member: 0,
                expireDate: 0,
                maxBook: 0,
                selectedBenefits: []
            });
            showError('Lỗi thêm gói');
        } finally {
            setSubmitting(false);
        }
    }


    return (
        <div style={{ margin: '0 200px' }}>
            <Notification />
            <div style={{ marginBottom: '20px' }}>
                <h5 >Thêm gói thành viên</h5>
            </div>
            <Form onSubmit={handleSubmit}>

                <TextInput
                    label="Tên gói"
                    name="nameSubscription"
                    type="text"
                    placeholder="Nhập tên gói"
                    value={formData.nameSubscription}
                    onChange={handleChange}
                />
                <TextInput
                    label="Thời hạn của gói ( tháng )"
                    name="expireDate"
                    type="number"
                    placeholder="Nhập thời hạn của gói"
                    value={formData.expireDate}
                    onChange={handleChange}
                />

                <TextInput
                    label="Giá"
                    name="fee_member"
                    type="number"
                    placeholder="Nhập giá"
                    value={formData.fee_member}
                    onChange={handleChange}
                />

                <TextInput
                    label="Số lượng sách có thể thuê trong 1 tháng ( max )"
                    name="maxBook"
                    type="number"
                    placeholder="Nhập giá"
                    value={formData.maxBook}
                    onChange={handleChange}
                />


                {benefits && <Form.Group className='mb-3'>
                    <Form.Label>Những điều khoản của gói thành viên</Form.Label>
                    {benefits.map((benefit, index) => (
                        <Form.Check
                            type="checkbox"
                            label={benefit?.name}
                            value={benefit?.id}
                            checked={formData.selectedBenefits.includes(benefit?.id + "")}
                            onChange={handleBenefitChange}
                            key={index}
                            style={{
                                fontSize: 'small',
                                margin: '0 10px'
                            }}
                        />
                    ))}
                </Form.Group>}


                <Button
                    type="submit"
                    style={{
                        fontSize: 'small',
                        backgroundColor: '#F87555',
                        border: 'none'
                    }}
                    disabled={submitting}
                >
                    {submitting ? <Spinner animation="border" size="sm" /> : 'Lưu thay đổi'}
                </Button>
            </Form>

        </div>
    )
}

export default AddMembership