import React, { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom';
import logo from './../../assets/image/Skateboarding.gif'
import { useAuth } from '../context/AuthContext';
import { enrollMembership } from '../../service/MemberShipService';

const PaymentConfirm = () => {
  const [searchParams] = useSearchParams();
  const { user } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const memberId = searchParams.get('vnp_OrderInfo');
    console.log(user);
    if(user){
      const body = {
        email: user?.email,
        membershipId: parseInt(memberId)
      }
  
      enrollMembership(body).then(resp => {
        console.log(resp);
          navigate('/', { state: { success: resp?.message } });
        
      })
    }

  }, [window.location, user])


  const loadingContainerStyle = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh', // Full height
  };

  const loadingImageStyle = {
    width: '100px', // Adjust the size as needed
    height: 'auto',
  };

  return (
    <div style={loadingContainerStyle}>
      <img src={logo} alt="Loading..." style={loadingImageStyle} />
    </div>
  );
}

export default PaymentConfirm