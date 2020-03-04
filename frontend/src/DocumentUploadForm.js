import React from 'react';
import { Redirect } from 'react-router-dom';
import { post } from 'axios';
import { Button, Form } from 'react-bootstrap';
import { ACCESS_TOKEN } from './Constants'; 

class DocumentUploadForm extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      goToChapterSelect: false,
      goToSummary: false,
      data: {}
    };

    this.handleSubmit = this.handleSubmit.bind(this);
    this.fileInput = React.createRef();
  }

  handleSubmit(e) {
    e.preventDefault();

    const formData = new FormData();
    formData.append("file", this.fileInput.current.files[0]);

    let config = { headers: { Authorization: 'Bearer ' + localStorage.getItem(ACCESS_TOKEN) }}
    
    post(this.props.endpoint, formData, config)
      .then(res => {
        if (res.data.pdfText !== '') {
          this.setState({ goToSummary: true });
        } else {
          this.setState({ goToChapterSelect: true, data: res.data });
        }
      });
  }

  render() {
    return (
      <div>
        <Form className="upload-form" onSubmit={this.handleSubmit} encType="multipart/form-data">
          <Form.Group controlId="uploadFile">
            <h4><Form.Label>Upload PDF</Form.Label></h4>
            <Form.Control type="file" ref={this.fileInput} name="file" />
          </Form.Group>
          <Button variant="primary" type="submit">Upload</Button>
        </Form>
        {this.state.goToChapterSelect && <Redirect to={{ pathname: "/chapter-select", state: { data: this.state.data } }} />}
        {this.state.goToSummary && <Redirect to={{ pathname: "/summary", state: { data: this.state.data } }} />}
      </div>
    );
  }
}

export default DocumentUploadForm;