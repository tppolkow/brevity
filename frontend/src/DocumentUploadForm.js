import React from 'react';
import { Redirect } from 'react-router-dom';
import { post, get } from 'axios';
import { Row, Col, Spinner } from 'react-bootstrap';
import Dropzone from 'react-dropzone';
import { ACCESS_TOKEN, BASE_URLS } from './Constants'; 
import './DocumentUploadForm.css';

class DocumentUploadForm extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      goToChapterSelect: false,
      goToSummary: false,
      data: {},
      uploading: false,
      userName: "",
    };

    this.handleDrop = this.handleDrop.bind(this);
    this.fileInput = React.createRef();
  }

  handleDrop(files) {
    this.setState({ uploading: true });

    const formData = new FormData();
    formData.append("file", files[0]);

    let config = { headers: { Authorization: 'Bearer ' + localStorage.getItem(ACCESS_TOKEN) }}
    
    post(BASE_URLS.serverUrl + '/upload', formData, config)
      .then(res => {
        if (res.data.pdfText !== '') {
          this.setState({ goToSummary: true, data: { summaryIds: { Summary: res.data.summaryId }} });
        } else {
          this.setState({ goToChapterSelect: true, data: res.data });
        }
      });
  }

  componentDidMount() {
    let config = { headers: { Authorization: 'Bearer ' + localStorage.getItem(ACCESS_TOKEN) }}
    get(BASE_URLS.serverUrl + '/auth/user', config).then(res => {
      this.setState({ userName: res.data }) 
    })
  }

  render() {
    return (
      <div>
        <Row>
          <Col lg={{span: 8, offset: 2}}>
            <h1>Summarizer</h1>
            <p className="blurb">
              Brevity is a tool for generating summaries from textbook PDFs.
              <br/>
              Start by uploading a PDF document below!
            </p>
            <h3>Upload a PDF</h3>
            <Dropzone onDrop={this.handleDrop} disabled={this.state.uploading}>
              {({getRootProps, getInputProps}) => (
                <section className="dnd-upload-container">
                  <div {...getRootProps()}>
                    <input {...getInputProps()} />
                    {
                      this.state.uploading ?
                        <div className="spinner-container">
                          <Spinner animation="border" role="status" variant="primary">
                            <span className="sr-only">Loading...</span>
                          </Spinner>
                        </div> :
                        <p>Drag 'n' drop some files here, or click to select files</p>
                    }
                  </div>
                </section>
              )}
            </Dropzone>
          </Col>
        </Row>
        {this.state.goToChapterSelect && <Redirect to={{ pathname: "/chapter-select", state: { data: this.state.data } }} />}
        {this.state.goToSummary && <Redirect to={{ pathname: "/summary", state: { data: this.state.data } }} />}
      </div>
    );
  }
}

export default DocumentUploadForm;