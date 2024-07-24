import React from 'react';
import { Select } from 'antd';
import '../../assets/style/Style.css';

const { Option } = Select;

const MultipleSelect = ({ name, value, onChange, options, placeholder, mode }) => {
  const handleChange = (selectedValue) => {
    console.log('name:', name, 'value:', selectedValue);
    onChange({ target: { name, value: selectedValue } });
  };

   // Tìm và hiển thị tên của các mục đã chọn
   const selectedValuesWithNames = value.map(val => {
    const selectedItem = options.find(item => item.id === val.id);
    return selectedItem ? selectedItem.id : val;
  });

  console.log('selectedValuesWithNames:', selectedValuesWithNames);

  return (
    <Select
      mode={mode}
      style={{
        width: '100%',
        marginBottom: '4px',
        fontSize: 'small'
      }}
      value={selectedValuesWithNames}
      placeholder={placeholder}
      onChange={handleChange}
      tokenSeparators={[',']}
    >
      {options.map((option) => (
        <Option key={option.id} value={option.id} style={{ fontSize: 'small' }}>
          {option.name}
        </Option>
      ))}
    </Select>
  );
};

export default MultipleSelect;
