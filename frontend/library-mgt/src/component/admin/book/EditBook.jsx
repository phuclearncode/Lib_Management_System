import React, { useState, useEffect } from 'react';
import { Form, Button, Row, Col, Spinner } from 'react-bootstrap';
import { useNavigate, useParams } from 'react-router-dom';
import useNotification from '../../../hooks/useNotification';
import Notification from '../../common/Notification';
import CustomSelect from '../../common/CustomSelect';
import MultipleSelect from '../../common/MultipleSelect';
import OptionSelect from '../../common/OptionSelect';
import ImageUpload from '../../common/ImageUpload';
import MultipleImageUpload from '../../common/MultipleImageUpload';
import TextInput from '../../common/TextInput';
import Textarea from '../../common/TextArea';
import { getAuthors } from '../../../service/AuthorService';
import { getCategories } from '../../../service/CategoryService';
import { useAuth } from '../../context/AuthContext';
import { updateBook, uploadBookImage, uploadBookSampleImages, getBookById, getBookImage, getBookSampleImages } from '../../../service/BookService';
import JSZip from 'jszip';

const EditBook = () => {
  const navigate = useNavigate();
  const { showError, showSuccess } = useNotification();
  const [authors, setAuthors] = useState([]);
  const [categories, setCategories] = useState([]);
  const { user } = useAuth();
  const { id } = useParams();
  const [submitting, setSubmitting] = useState(false);



  useEffect(() => {
    const fetchAuthors = async () => {
      try {
        const response = await getAuthors();
        if (response.status === 200) {
          setAuthors(response.data);
        } else {
          showError(response.message);
        }
      } catch (error) {
        console.error("Lỗi khi lấy tác giả:", error);
        showError('Lỗi khi lấy tác giả');
      }
    };

    fetchAuthors();
  }, []);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await getCategories();
        if (response.status === 200) {
          setCategories(response.data);
        } else {
          showError(response.message);
        }
      } catch (error) {
        console.error("Lỗi khi lấy danh mục:", error);
        showError('Lỗi khi lấy danh mục');
      }
    };

    fetchCategories();
  }, []);

  console.log('Categories sssss:', categories);

  const status = [
    { name: 'Có sẵn', value: 'ACTIVE' },
    { name: 'Không hoạt động', value: 'INACTIVE' },
    { name: 'Hết hàng', value: 'OUT_OF_STOCK' }
  ];

  const languages = [
    { name: 'Tiếng Anh', value: 'English' },
    { name: 'Tiếng Việt', value: 'Vietnamese' },
    { name: 'Tiếng Nhật', value: 'Japanese' },
    { name: 'Tiếng Tây Ban Nha', value: 'Spanish' },
    { name: 'Tiếng Trung Quốc', value: 'Chinese' },
    { name: 'Tiếng Pháp', value: 'French' },
    { name: 'Tiếng Đức', value: 'German' },
    { name: 'Tiếng Nga', value: 'Russian' },
    { name: 'Tiếng Bồ Đào Nha', value: 'Portuguese' },
    { name: 'Tiếng Ý', value: 'Italian' },
    { name: 'Tiếng Hàn', value: 'Korean' },
    { name: 'Tiếng Hindi', value: 'Hindi' },
    { name: 'Tiếng Ả Rập', value: 'Arabic' },
    { name: 'Tiếng Hà Lan', value: 'Dutch' },
    { name: 'Tiếng Thụy Điển', value: 'Swedish' },
    { name: 'Tiếng Thổ Nhĩ Kỳ', value: 'Turkish' },
    { name: 'Tiếng Ba Lan', value: 'Polish' },
    { name: 'Tiếng Thái', value: 'Thai' }
  ];


  const [bookData, setBookData] = useState({
    userId: user.id,
    isbn: '',
    title: '',
    price: '',
    totalPage: '',
    status: 'ACTIVE',
    publisher: '',
    publicationYear: '',
    language: '',
    description: '',
    authors: [],
    categories: []
  });

  const [bookImage, setBookImage] = useState(null);

  const [bookSampleImages, setBookSampleImages] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const book = await getBookById(id);
        if (book.status === 200) {
          setBookData({
            userId: user.id,
            isbn: book.data.isbn,
            title: book.data.title,
            price: book.data.price,
            totalPage: book.data.totalPage,
            status: book.data.status,
            publisher: book.data.publisher,
            publicationYear: book.data.publicationYear,
            language: book.data.language,
            description: book.data.description,
            authors: book.data.authors.map(author => author.id),
            categories: book.data.categories.map(category => category.id)
          });
        } else {
          showError(book.message);
        }
      } catch (error) {
        console.error("Error fetching book:", error);
      }
    };

    fetchData();
  }, [id]);

  useEffect(() => {
    const fetchBookImage = async () => {
      try {
        const response = await getBookImage(id);
        console.log('Response image:', response);
        if (response.status === 200) {
          const contentDisposition = response.headers['content-disposition'];
          console.log('Content-Disposition:', contentDisposition);
          let fileName = 'unknown';
          if (contentDisposition) {
            const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
            if (fileNameMatch.length === 2) {
              fileName = fileNameMatch[1];
            }
          }
          const file = new File([response.data], fileName, { type: response.data.type });
          console.log('File:', file);
          setBookImage(file);
        } else {
          console.error('Lỗi: Dữ liệu không phải Blob');
        }
      } catch (error) {
        console.error('Lỗi khi lấy ảnh sách:', error);
      }
    };

    fetchBookImage();
  }, [id]);


  useEffect(() => {
    const fetchBookSampleImages = async () => {
      try {
        const sampleImagesResponse = await getBookSampleImages(id);
        if (sampleImagesResponse.status === 200) {
          const contentDisposition = sampleImagesResponse.headers['content-disposition'];
          let zipFileName = 'unknown';
          if (contentDisposition) {
            const fileNameMatch = contentDisposition.match(/filename="(.+)"/);
            if (fileNameMatch && fileNameMatch.length === 2) {
              zipFileName = fileNameMatch[1];
            }
          }

          const sampleImagesBlob = new File([sampleImagesResponse.data], zipFileName, { type: sampleImagesResponse.data.type });
          const zip = new JSZip();

          zip.loadAsync(sampleImagesBlob).then(async (zip) => {
            const imageUrls = [];
            await Promise.all(Object.keys(zip.files).map(async (filename) => {
              const fileData = await zip.file(filename).async('blob');
              const imageUrl = URL.createObjectURL(fileData);
              imageUrls.push({ url: imageUrl, name: filename });
            }));

            setBookSampleImages(imageUrls);
          }).catch((error) => {
            console.error("Lỗi giải nén file zip:", error);
            showError("Lỗi giải nén file zip");
          });
        } else {
          showError(sampleImagesResponse.message);
        }
      } catch (error) {
        console.error("Lỗi khi lấy ảnh xem trước sách:", error);
        showError("Lỗi khi lấy ảnh xem trước sách");
      }

    };

    fetchBookSampleImages();
  }, [id]);

  const handleChange = (e) => {
    const { name, value, files } = e.target;

    if (name === 'bookImage') {
      setBookImage(files || null);
    } else if (name === 'bookSampleImages') {
      setBookSampleImages(files || []);
    } else {
      setBookData({ ...bookData, [name]: value });
    }
  };

  console.log('Submitting book data:', bookData);
  console.log('Book image:', bookImage);
  console.log('Book sample images:', bookSampleImages);

  const validateForm = () => {
    const { isbn, title, price, totalPage, publicationYear, language, authors } = bookData;
    if (!isbn || !title || !price || !totalPage || !publicationYear || !language || authors.length === 0) {
      showError('Vui lòng nhập đầy đủ thông tin');
      return false;
    } else if (!bookImage) {
      showError('Vui lòng chọn ảnh sách');
      return false;
    } else if (bookSampleImages.length === 0) {
      showError('Vui lòng chọn ít nhất một mẫu ảnh xem trước');
      return false;
    }

    return true;
  };


  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setSubmitting(true);
    const timer = new Promise(resolve => setTimeout(resolve, 2000));

    try {
      const response = await updateBook(id, bookData);
      await timer;

      console.log('Response update book:', response);

      if (response.status === 200) {
        showSuccess('Cập nhật sách thành công');
        setBookData({
          userId: user.id,
          isbn: '',
          title: '',
          price: '',
          totalPage: '',
          status: 'ACTIVE',
          publisher: '',
          publicationYear: '',
          language: '',
          description: '',
          authors: [],
          categories: []
        });


        if (bookImage) {
          await uploadBookImage(response.data, bookImage);
        }

        if (bookSampleImages.length > 0) {
          await uploadBookSampleImages(response.data, bookSampleImages);
        }

        navigate('/admin/book', { state: { success: response.message } });
      } else {
        showError(response.message);
      }
    } catch (error) {
      console.error('Lỗi khi cập nhật sách:', error);
      showError('Lỗi khi cập nhật sách');
    } finally {
      setSubmitting(false);
    }

  }

  return (
    <div style={{ margin: '0 200px' }}>
      <Notification />
      <div style={{ marginBottom: '20px' }}>
        <h5>Cập nhật sách</h5>
      </div>
      <Form onSubmit={handleSubmit}>
        <Row>
          <Col md={7}>
            <TextInput
              label="ISBN"
              name="isbn"
              value={bookData.isbn}
              onChange={handleChange}
              placeholder="Nhập ISBN"
              type="text"
            />

            <TextInput
              label="Tiêu đề sách"
              name="title"
              value={bookData.title}
              onChange={handleChange}
              placeholder="Nhập tiêu đề sách"
              type="text"
            />

            <Row>
              <Col>
                <TextInput
                  label="Giá"
                  name="price"
                  value={bookData.price}
                  onChange={handleChange}
                  placeholder="Nhập giá sách"
                  type="text"
                />
              </Col>
              <Col>
                <TextInput
                  label="Số trang"
                  name="totalPage"
                  value={bookData.totalPage}
                  onChange={handleChange}
                  placeholder="Nhập số trang"
                  type="text"
                />
              </Col>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label className="label">Trạng thái</Form.Label>
                  <CustomSelect
                    name="status"
                    value={bookData.status}
                    onChange={handleChange}
                    data={status}
                    placeholder="Chọn trạng thái"
                    valueType="value"
                  />
                </Form.Group>
              </Col>
            </Row>

            <TextInput
              label="Nhà xuất bản"
              name="publisher"
              value={bookData.publisher}
              onChange={handleChange}
              placeholder="Nhập nhà xuất bản"
              type="text"
            />

            <Row>
              <Col>
                <TextInput
                  label="Năm xuất bản"
                  name="publicationYear"
                  value={bookData.publicationYear}
                  onChange={handleChange}
                  placeholder="Nhập năm xuất bản"
                  type="text"
                />
              </Col>
              <Col>
                <Form.Group className="mb-3">
                  <Form.Label className="label">Ngôn ngữ</Form.Label>
                  <CustomSelect
                    name="language"
                    value={bookData.language}
                    onChange={handleChange}
                    data={languages}
                    placeholder="Chọn ngôn ngữ"
                  />
                </Form.Group>
              </Col>
            </Row>

            <Textarea
              label="Mô tả"
              name="description"
              value={bookData.description}
              onChange={handleChange}
              placeholder="Nhập mô tả"
              rows={10}
            />
          </Col>

          <Col md={5}>
            <Form.Group className="mb-3">
              <Form.Label className="label">Tác giả</Form.Label>
              <MultipleSelect
                name="authors"
                value={bookData.authors}
                onChange={handleChange}
                options={authors}
                placeholder="Chọn tác giả"
                mode="tags"
              />
            </Form.Group>

            <Form.Group className="mb-3">
              <Form.Label className="label">Danh mục</Form.Label>
              <OptionSelect
                name="categories"
                value={bookData.categories}
                onChange={handleChange}
                data={categories}
                placeholder="Chọn danh mục"
                mode="tags"
              />
            </Form.Group>

            <ImageUpload
              label="Tải ảnh sách"
              name="bookImage"
              onChange={handleChange}
              showError={showError}
              defaultValue={bookImage}
            />

            <MultipleImageUpload
              label="Tải ảnh đọc thử"
              name="bookSampleImages"
              onChange={handleChange}
              showError={showError}
              defaultValue={bookSampleImages}
            />

          </Col>
        </Row>
        <Button
          type='submit'
          style={{ fontSize: 'small', backgroundColor: '#F87555', border: 'none' }}
          disabled={submitting}
        >
          {submitting ? <Spinner animation="border" size="sm" /> : "Lưu thay đổi"}
        </Button>
      </Form>
    </div>
  );
}

export default EditBook;
