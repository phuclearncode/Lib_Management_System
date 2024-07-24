import React from 'react';
import { Select } from 'antd';

const { Option } = Select;

const CustomSelect = ({ name, value, onChange, placeholder, data, valueType }) => {
    const handleChange = (selectedValue) => {
        console.log('name:', name, 'value:', selectedValue);
        onChange({ target: { name, value: selectedValue } });
    };

    const handleSearch = (searchValue) => {
        console.log('Search:', searchValue);
    };
    
    return (
        <Select
            showSearch
            style={{
                width: '100%',
                marginBottom: '4px',
                fontSize: 'small'
            }}
            placeholder={placeholder}
            value={value ? value : placeholder}
            onChange={handleChange}
            onSearch={handleSearch}
            filterOption={(input, option) =>
                option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
            }
            allowClear
        >
            {data && data.map(option => (
                <Option key={option.id} value={valueType === 'id' ? option.id : option.value} style={{ fontSize: 'small' }}>
                    {option.name}
                </Option>
            ))}
        </Select>
    );
};

export default CustomSelect;
